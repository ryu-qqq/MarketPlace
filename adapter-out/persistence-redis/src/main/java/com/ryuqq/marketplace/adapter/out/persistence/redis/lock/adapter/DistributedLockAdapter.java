package com.ryuqq.marketplace.adapter.out.persistence.redis.lock.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.redis.common.exception.LockAcquisitionException;
import com.ryuqq.marketplace.application.common.port.out.DistributedLockPort;
import com.ryuqq.marketplace.domain.common.vo.LockKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * Redisson 분산락 Adapter
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>분산락 획득/해제
 *   <li>Lock 상태 관리
 *   <li>Thread-safe Lock 인스턴스 관리
 * </ul>
 *
 * <p><strong>금지 사항:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 포함 금지
 *   <li>DB 접근 금지
 *   <li>@Transactional 금지
 * </ul>
 *
 * <p><strong>Pub/Sub 기반 Lock:</strong>
 *
 * <p>Redisson은 Pub/Sub 이벤트 리스너 방식으로 Lock을 대기합니다. Lettuce의 SETNX + 스핀락 방식과 달리 CPU/네트워크 효율적입니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@Component
public class DistributedLockAdapter implements DistributedLockPort {

    private final RedissonClient redissonClient;

    /**
     * Thread-safe Lock 인스턴스 캐시
     *
     * <p>같은 키에 대해 동일한 Lock 인스턴스를 반환합니다.
     */
    private final ConcurrentHashMap<String, RLock> lockCache;

    public DistributedLockAdapter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.lockCache = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>InterruptedException 처리:</strong>
     *
     * <p>인터럽트 발생 시 스레드 인터럽트 상태를 복원하고 LockAcquisitionException을 던집니다.
     *
     * @throws LockAcquisitionException Lock 획득 중 인터럽트 발생 시
     */
    @Override
    public boolean tryLock(LockKey key, long waitTime, long leaseTime, TimeUnit unit) {
        String keyValue = key.value();
        RLock lock = getLock(keyValue);

        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException(
                    keyValue, String.format("Lock 획득 중 인터럽트 발생: key=%s", keyValue), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>안전한 unlock:</strong>
     *
     * <p>현재 스레드가 Lock을 보유한 경우에만 해제합니다. 다른 스레드가 보유한 Lock을 해제하지 않습니다.
     */
    @Override
    public void unlock(LockKey key) {
        RLock lock = getLock(key.value());

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHeldByCurrentThread(LockKey key) {
        RLock lock = getLock(key.value());
        return lock.isHeldByCurrentThread();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLocked(LockKey key) {
        RLock lock = getLock(key.value());
        return lock.isLocked();
    }

    /**
     * Lock 인스턴스 조회 (캐싱)
     *
     * <p>같은 키에 대해 동일한 RLock 인스턴스를 반환합니다.
     *
     * @param keyValue Lock 키 문자열
     * @return RLock 인스턴스
     */
    private RLock getLock(String keyValue) {
        return lockCache.computeIfAbsent(keyValue, redissonClient::getLock);
    }
}
