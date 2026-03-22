package com.ryuqq.marketplace.adapter.out.client.naver.support;

import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 API 실행기.
 *
 * <p>CB(바깥) + Retry(안쪽) 조합으로 안전하게 API를 호출합니다.
 *
 * <ul>
 *   <li>CB OPEN → {@link ExternalServiceUnavailableException} 즉시 반환
 *   <li>재시도 대상 예외: {@link NaverCommerceServerException}, {@link NaverCommerceRateLimitException},
 *       {@link NaverCommerceNetworkException}
 *   <li>재시도 제외 예외: {@link NaverCommerceBadRequestException}, {@link NaverCommerceClientException}
 *   <li>Exponential backoff: 100ms → 200ms → 400ms (최대 3회)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceApiExecutor {

    private static final String RETRY_NAME = "naverCommerce";
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MILLIS = 100L;

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public NaverCommerceApiExecutor(CircuitBreaker naverCommerceCircuitBreaker) {
        this.circuitBreaker = naverCommerceCircuitBreaker;
        this.retry = buildRetry();
    }

    /**
     * CB + Retry 조합으로 Supplier를 실행합니다.
     *
     * @param supplier 실행할 API 호출 로직
     * @param <T> 반환 타입
     * @return API 호출 결과
     * @throws ExternalServiceUnavailableException CB OPEN 상태일 때
     */
    public <T> T execute(Supplier<T> supplier) {
        Supplier<T> retryDecorated = Retry.decorateSupplier(retry, supplier);
        try {
            return circuitBreaker.executeSupplier(retryDecorated);
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "네이버 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    /**
     * CB + Retry 조합으로 Runnable을 실행합니다.
     *
     * @param runnable 실행할 API 호출 로직
     * @throws ExternalServiceUnavailableException CB OPEN 상태일 때
     */
    public void execute(Runnable runnable) {
        execute(
                () -> {
                    runnable.run();
                    return null;
                });
    }

    private static Retry buildRetry() {
        RetryConfig retryConfig =
                RetryConfig.custom()
                        .maxAttempts(MAX_ATTEMPTS)
                        .intervalFunction(
                                attempt -> INITIAL_BACKOFF_MILLIS * (long) Math.pow(2, attempt - 1))
                        .retryExceptions(
                                NaverCommerceServerException.class,
                                NaverCommerceRateLimitException.class,
                                NaverCommerceNetworkException.class)
                        .ignoreExceptions(
                                NaverCommerceBadRequestException.class,
                                NaverCommerceClientException.class)
                        .build();

        return RetryRegistry.of(retryConfig).retry(RETRY_NAME);
    }
}
