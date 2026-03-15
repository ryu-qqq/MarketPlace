package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 클라이언트 에러 (4xx, 400 제외).
 *
 * <p>Circuit Breaker에서 무시됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceClientException extends SetofCommerceException {

    public SetofCommerceClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
