package com.ryuqq.marketplace.adapter.out.client.sellic.exception;

/** 셀릭 커머스 API 429 Rate Limit. CB 기록, retryable=true. */
public class SellicCommerceRateLimitException extends SellicCommerceException {

    public SellicCommerceRateLimitException(String responseBody) {
        super(429, responseBody, ErrorType.RATE_LIMIT);
    }
}
