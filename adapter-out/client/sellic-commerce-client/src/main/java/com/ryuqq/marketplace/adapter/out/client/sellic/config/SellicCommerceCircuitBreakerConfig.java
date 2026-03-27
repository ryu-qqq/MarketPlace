package com.ryuqq.marketplace.adapter.out.client.sellic.config;

import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceServerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 셀릭 커머스 Circuit Breaker 설정.
 *
 * <p>셀릭 커머스 API 장애 시 빠른 실패(fail-fast)를 통해 스레드 블로킹을 방지합니다.
 *
 * <ul>
 *   <li>실패율 50% 초과 시 OPEN
 *   <li>Slow call(3초 이상)이 80% 초과 시에도 OPEN
 *   <li>OPEN 상태에서 60초 대기 후 HALF_OPEN
 *   <li>HALF_OPEN에서 5건 시도 후 복구 판정
 *   <li>Sliding window: 최근 20건 기준 (COUNT_BASED)
 * </ul>
 */
@Configuration
@ConditionalOnProperty(prefix = "sellic-commerce", name = "base-url")
public class SellicCommerceCircuitBreakerConfig {

    private static final String CIRCUIT_BREAKER_NAME = "sellicCommerce";
    private static final int FAILURE_RATE_THRESHOLD = 50;
    private static final Duration SLOW_CALL_DURATION_THRESHOLD = Duration.ofSeconds(3);
    private static final int SLOW_CALL_RATE_THRESHOLD = 80;
    private static final int SLIDING_WINDOW_SIZE = 20;
    private static final int MINIMUM_NUMBER_OF_CALLS = 10;
    private static final int PERMITTED_CALLS_IN_HALF_OPEN = 5;
    private static final Duration WAIT_DURATION_IN_OPEN = Duration.ofSeconds(60);

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Bean
    public CircuitBreaker sellicCommerceCircuitBreaker() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                        .slowCallDurationThreshold(SLOW_CALL_DURATION_THRESHOLD)
                        .slowCallRateThreshold(SLOW_CALL_RATE_THRESHOLD)
                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(SLIDING_WINDOW_SIZE)
                        .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                        .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                        .waitDurationInOpenState(WAIT_DURATION_IN_OPEN)
                        .recordExceptions(
                                SellicCommerceServerException.class,
                                SellicCommerceRateLimitException.class,
                                SellicCommerceNetworkException.class)
                        .ignoreExceptions(
                                SellicCommerceBadRequestException.class,
                                SellicCommerceClientException.class)
                        .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        if (meterRegistry != null) {
            TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(registry).bindTo(meterRegistry);
        }

        return registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
    }
}
