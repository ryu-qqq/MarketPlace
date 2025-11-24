package com.ryuqq.marketplace.application.common.config;

import com.ryuqq.marketplace.domain.common.util.ClockHolder;
import java.time.Clock;

/**
 * System Clock을 제공하는 ClockHolder 구현체 (Production)
 *
 * <p>Application Layer에서 Domain Layer에 Clock을 제공하기 위한 구현체입니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>✅ ClockHolder 인터페이스 구현 (DIP)
 *   <li>✅ Production 환경에서 사용 (System.currentTimeMillis() 기반)
 *   <li>✅ Spring Bean으로 등록하여 Assembler에 주입
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // Config
 * @Configuration
 * public class ClockConfig {
 *     @Bean
 *     public ClockHolder clockHolder() {
 *         return new SystemClockHolder();
 *     }
 * }
 *
 * // Assembler
 * @Component
 * public class OrderAssembler {
 *     private final ClockHolder clockHolder;
 *
 *     public Order toAggregate(PlaceOrderCommand command) {
 *         return Order.forNew(clockHolder.getClock(), command.amount());
 *     }
 * }
 * }</pre>
 *
 * @author ryu-qqq
 * @since 2025-11-21
 */
public final class SystemClockHolder implements ClockHolder {

    /**
     * System Clock 반환
     *
     * <p>UTC 기준 시스템 시간을 제공합니다.
     *
     * @return Clock.systemUTC() (Production 환경 시간)
     * @author ryu-qqq
     * @since 2025-11-21
     */
    @Override
    public Clock getClock() {
        return Clock.systemUTC();
    }
}
