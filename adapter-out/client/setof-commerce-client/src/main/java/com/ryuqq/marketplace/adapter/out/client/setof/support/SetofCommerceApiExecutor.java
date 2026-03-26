package com.ryuqq.marketplace.adapter.out.client.setof.support;

import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceUnauthorizedException;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.Duration;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 API 실행기.
 *
 * <p>CB(바깥) + Retry(안쪽) 조합으로 외부 API 호출을 보호합니다.
 *
 * <ul>
 *   <li>CB가 바깥: Retry 전체 실패 후 1건으로 CB에 기록 → 오탐 방지
 *   <li>Retry: maxAttempts=3, initialBackoff=100ms, exponential
 *   <li>retryOn: {@link SetofCommerceServerException}, {@link SetofCommerceRateLimitException},
 *       {@link SetofCommerceNetworkException}
 *   <li>ignoreOn: {@link SetofCommerceBadRequestException}, {@link SetofCommerceClientException},
 *       {@link SetofCommerceUnauthorizedException}
 *   <li>CB OPEN 시: {@link ExternalServiceUnavailableException} 던짐
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceApiExecutor {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceApiExecutor.class);

    private static final String RETRY_NAME = "setofCommerce";
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration INITIAL_BACKOFF = Duration.ofMillis(100);

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public SetofCommerceApiExecutor(CircuitBreaker setofCommerceCircuitBreaker) {
        this.circuitBreaker = setofCommerceCircuitBreaker;
        this.retry = buildRetry();
    }

    /**
     * CB(바깥) + Retry(안쪽) 조합으로 Supplier 실행.
     *
     * <p>CB OPEN 시 {@link ExternalServiceUnavailableException}을 던집니다.
     *
     * @param supplier 실행할 로직
     * @return 실행 결과
     */
    public <T> T execute(Supplier<T> supplier) {
        Supplier<T> retrySupplier = Retry.decorateSupplier(retry, supplier);
        try {
            return circuitBreaker.executeSupplier(retrySupplier);
        } catch (CallNotPermittedException e) {
            log.warn("세토프 커머스 Circuit Breaker OPEN - 요청 차단됨");
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    /**
     * CB(바깥) + Retry(안쪽) 조합으로 Runnable 실행.
     *
     * <p>CB OPEN 시 {@link ExternalServiceUnavailableException}을 던집니다.
     *
     * @param runnable 실행할 로직
     */
    public void execute(Runnable runnable) {
        execute(
                () -> {
                    runnable.run();
                    return null;
                });
    }

    private Retry buildRetry() {
        RetryConfig config =
                RetryConfig.custom()
                        .maxAttempts(MAX_ATTEMPTS)
                        .waitDuration(INITIAL_BACKOFF)
                        .retryExceptions(
                                SetofCommerceServerException.class,
                                SetofCommerceRateLimitException.class,
                                SetofCommerceNetworkException.class)
                        .ignoreExceptions(
                                SetofCommerceBadRequestException.class,
                                SetofCommerceClientException.class,
                                SetofCommerceUnauthorizedException.class)
                        .build();

        return RetryRegistry.of(config).retry(RETRY_NAME);
    }
}
