package com.ryuqq.marketplace.adapter.out.client.setof.config;

import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
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
 * 세토프 커머스 Circuit Breaker 설정.
 *
 * <p>세토프 커머스 API 장애 시 빠른 실패(fail-fast)를 통해 스레드 블로킹을 방지합니다.
 *
 * <ul>
 *   <li>실패율 50% 초과 시 OPEN
 *   <li>Slow call(3초 이상)이 80% 초과 시에도 OPEN
 *   <li>OPEN 상태에서 60초 대기 후 HALF_OPEN
 *   <li>HALF_OPEN에서 5건 시도 후 복구 판정
 *   <li>Sliding window: 최근 20건 기준 (COUNT_BASED)
 *   <li>{@link SetofCommerceServerException}(5xx)만 실패로 기록
 *   <li>{@link SetofCommerceBadRequestException}(400), {@link SetofCommerceClientException}(4xx)은
 *       무시
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceCircuitBreakerConfig {

    private static final String CIRCUIT_BREAKER_NAME = "setofCommerce";

    /** 실패율 임계값 (%). 이 값을 초과하면 OPEN. */
    private static final int FAILURE_RATE_THRESHOLD = 50;

    /** Slow call 판정 기준 시간. read timeout(30초)의 1/10. */
    private static final Duration SLOW_CALL_DURATION_THRESHOLD = Duration.ofSeconds(3);

    /** Slow call 비율 임계값 (%). 이 값을 초과하면 OPEN. */
    private static final int SLOW_CALL_RATE_THRESHOLD = 80;

    /** Sliding window 크기. 통계적 유의미성과 판정 속도의 균형. */
    private static final int SLIDING_WINDOW_SIZE = 20;

    /** CB 판정 시작 전 최소 호출 수. 콜드스타트 오탐 방지. */
    private static final int MINIMUM_NUMBER_OF_CALLS = 10;

    /** HALF_OPEN 상태에서 복구 확인용 호출 수. */
    private static final int PERMITTED_CALLS_IN_HALF_OPEN = 5;

    /** OPEN → HALF_OPEN 전이 대기 시간. */
    private static final Duration WAIT_DURATION_IN_OPEN = Duration.ofSeconds(60);

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Bean
    public CircuitBreaker setofCommerceCircuitBreaker() {
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
                        .recordExceptions(SetofCommerceServerException.class)
                        .ignoreExceptions(
                                SetofCommerceBadRequestException.class,
                                SetofCommerceClientException.class)
                        .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        if (meterRegistry != null) {
            TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(registry).bindTo(meterRegistry);
        }

        return registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
    }
}
