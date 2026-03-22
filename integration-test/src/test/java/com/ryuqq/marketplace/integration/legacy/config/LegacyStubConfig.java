package com.ryuqq.marketplace.integration.legacy.config;

import com.ryuqq.marketplace.application.common.port.out.DistributedLockPort;
import com.ryuqq.marketplace.application.legacy.order.port.out.command.LegacyShipmentCommandPort;
import com.ryuqq.marketplace.application.shadow.dto.ShadowSnapshot;
import com.ryuqq.marketplace.application.shadow.port.out.ShadowSnapshotStore;
import com.ryuqq.marketplace.domain.common.vo.LockKey;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 레거시 API E2E 테스트용 인프라 Stub 설정.
 *
 * <p>LegacyApiApplication 컨텍스트 로드 시 필요하지만 H2 테스트 환경에서 제공할 수 없는 빈들을 Stub으로 대체합니다.
 *
 * <ul>
 *   <li>ShadowSnapshotStore: Shadow 트래픽 기능 비활성화 (no-op stub)
 *   <li>LegacyShipmentCommandPort: 미구현 Port stub (배송 관련 기능 미완성)
 *   <li>DistributedLockPort: Redis 분산락 stub (테스트 환경에서 Redis 불필요)
 * </ul>
 *
 * <p>RedissonClient, QnaOutboxPublishClient, QnaAnswerSyncStrategy stub은 StubExternalClientConfig에서
 * 제공됩니다.
 *
 * <p>레거시 Flyway 마이그레이션은 persistence.legacy.flyway.locations 프로퍼티로 실행을 억제합니다.
 * LegacyE2ETestBase의 @TestPropertySource 에서 설정합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@TestConfiguration
public class LegacyStubConfig {

    /**
     * ShadowSnapshotStore no-op stub.
     *
     * <p>테스트 환경에서는 Shadow 트래픽 스냅샷 저장이 불필요합니다.
     */
    @Bean
    @Primary
    public ShadowSnapshotStore stubShadowSnapshotStore() {
        return new ShadowSnapshotStore() {
            @Override
            public void save(ShadowSnapshot snapshot) {
                // stub: no-op
            }

            @Override
            public Optional<ShadowSnapshot> findByCorrelationId(String correlationId) {
                return Optional.empty();
            }
        };
    }

    /**
     * LegacyShipmentCommandPort no-op stub.
     *
     * <p>레거시 배송 커맨드 Port는 아직 adapter-out 구현체가 없어 stub으로 제공합니다.
     */
    @Bean
    public LegacyShipmentCommandPort stubLegacyShipmentCommandPort() {
        return (orderId, invoiceNo, courierCode, shipmentType) -> {
            // stub: no-op
        };
    }

    /**
     * DistributedLockPort no-op stub.
     *
     * <p>테스트 환경에서는 분산락을 항상 성공으로 처리합니다. LegacyConversionSeederLockManager,
     * LegacyOrderSeederLockManager 등이 의존합니다.
     */
    @Bean
    @Primary
    public DistributedLockPort stubDistributedLockPort() {
        return new DistributedLockPort() {
            @Override
            public boolean tryLock(LockKey key, long waitTime, long leaseTime, TimeUnit unit) {
                return true;
            }

            @Override
            public void unlock(LockKey key) {
                // stub: no-op
            }

            @Override
            public boolean isHeldByCurrentThread(LockKey key) {
                return false;
            }

            @Override
            public boolean isLocked(LockKey key) {
                return false;
            }
        };
    }
}
