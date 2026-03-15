package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 Rate Limit 예외 (HTTP 429).
 *
 * <p>Circuit Breaker에서 실패로 기록됩니다. 요청 제한 초과를 나타내며, 계속 요청하면 상황이 악화되므로 CB로 차단해야 합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceRateLimitException extends NaverCommerceException {

    public NaverCommerceRateLimitException(String responseBody) {
        super(429, responseBody);
    }
}
