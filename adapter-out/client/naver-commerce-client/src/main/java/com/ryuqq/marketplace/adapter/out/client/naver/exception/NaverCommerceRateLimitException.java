package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 Rate Limit 예외 (HTTP 429).
 *
 * <p>Circuit Breaker에서 실패로 기록됩니다. 요청 제한 초과를 나타내며, 재시도 대상입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceRateLimitException extends NaverCommerceException {

    public NaverCommerceRateLimitException(String responseBody) {
        super(429, responseBody, ErrorType.RATE_LIMIT);
    }
}
