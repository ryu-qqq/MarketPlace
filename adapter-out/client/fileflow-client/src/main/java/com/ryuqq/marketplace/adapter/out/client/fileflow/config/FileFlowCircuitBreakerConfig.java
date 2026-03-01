package com.ryuqq.marketplace.adapter.out.client.fileflow.config;

import com.ryuqq.fileflow.sdk.exception.FileFlowBadRequestException;
import com.ryuqq.fileflow.sdk.exception.FileFlowServerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FileFlow Circuit Breaker 설정.
 *
 * <p>FileFlow 서버 장애 시 빠른 실패(fail-fast)를 통해 스케줄러 스레드 블로킹을 방지합니다.
 *
 * <ul>
 *   <li>실패율 50% 초과 시 OPEN
 *   <li>HALF_OPEN에서 3건 시도 후 성공/실패 판정
 *   <li>Sliding window: 최근 10건 기준
 *   <li>{@link FileFlowServerException}만 실패로 기록
 *   <li>{@link FileFlowBadRequestException}은 무시 (400은 장애가 아님)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "fileflow", name = "base-url")
public class FileFlowCircuitBreakerConfig {

    private static final String CIRCUIT_BREAKER_NAME = "fileflow";
    private static final int FAILURE_RATE_THRESHOLD = 50;
    private static final int SLIDING_WINDOW_SIZE = 10;
    private static final int MINIMUM_NUMBER_OF_CALLS = 5;
    private static final int PERMITTED_CALLS_IN_HALF_OPEN = 3;
    private static final Duration WAIT_DURATION_IN_OPEN = Duration.ofSeconds(30);

    @Bean
    public CircuitBreaker fileFlowCircuitBreaker() {
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(SLIDING_WINDOW_SIZE)
                        .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                        .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                        .waitDurationInOpenState(WAIT_DURATION_IN_OPEN)
                        .recordExceptions(FileFlowServerException.class)
                        .ignoreExceptions(FileFlowBadRequestException.class)
                        .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        return registry.circuitBreaker(CIRCUIT_BREAKER_NAME);
    }
}
