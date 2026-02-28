package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.common.port.out.DistributedLockPort;
import com.ryuqq.marketplace.domain.common.vo.LockKey;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 벌크 시딩 분산 락 Manager.
 *
 * <p>ECS 다중 인스턴스 환경에서 Seeder 스케줄러가 동시 실행되는 것을 방지합니다. Seeder는 Read → 없으면 INSERT 패턴이라 CAS(낙관적 락)로 보호할
 * 수 없어 스케줄러 실행 단위로 분산 락을 겁니다.
 */
@Component
public class LegacyConversionSeederLockManager {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyConversionSeederLockManager.class);

    private static final long WAIT_SECONDS = 0;
    private static final long LEASE_SECONDS = 300;

    private final DistributedLockPort lockPort;

    public LegacyConversionSeederLockManager(DistributedLockPort lockPort) {
        this.lockPort = lockPort;
    }

    /**
     * 분산 락을 획득한 후 작업을 실행합니다.
     *
     * <p>락 획득 실패 시 경고 로그를 남기고 fallback 값을 반환합니다.
     *
     * @param action 락 획득 성공 시 실행할 작업
     * @param fallback 락 획득 실패 시 반환할 값
     * @param <T> 반환 타입
     * @return 작업 결과 또는 fallback 값
     */
    public <T> T executeWithLock(Supplier<T> action, T fallback) {
        LockKey lockKey = new SeederLockKey();
        boolean acquired = lockPort.tryLock(lockKey, WAIT_SECONDS, LEASE_SECONDS, TimeUnit.SECONDS);

        if (!acquired) {
            log.warn("레거시 시딩 분산 락 획득 실패 - 다른 인스턴스가 실행 중. 이번 주기 스킵");
            return fallback;
        }

        try {
            return action.get();
        } finally {
            lockPort.unlock(lockKey);
        }
    }

    private record SeederLockKey() implements LockKey {
        private static final String KEY = "lock:legacy-conversion:seeder";

        @Override
        public String value() {
            return KEY;
        }
    }
}
