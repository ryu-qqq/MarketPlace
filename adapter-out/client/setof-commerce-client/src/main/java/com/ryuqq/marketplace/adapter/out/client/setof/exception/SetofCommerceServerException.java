package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 서버 에러 (5xx).
 *
 * <p>Circuit Breaker에 실패로 기록됩니다. retryable=true.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceServerException extends SetofCommerceException {

    public SetofCommerceServerException(int statusCode, String responseBody) {
        super(statusCode, responseBody, ErrorType.SERVER_ERROR);
    }
}
