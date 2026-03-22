package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 요청 제한 에러 (429 Too Many Requests).
 *
 * <p>Circuit Breaker에 실패로 기록됩니다. 계속 보내면 상황이 악화되므로 CB로 차단 필요. retryable=true.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceRateLimitException extends SetofCommerceException {

    public SetofCommerceRateLimitException(String responseBody) {
        super(429, responseBody, ErrorType.RATE_LIMIT);
    }
}
