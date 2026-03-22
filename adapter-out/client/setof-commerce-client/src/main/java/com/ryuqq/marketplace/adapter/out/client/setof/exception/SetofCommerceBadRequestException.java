package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 잘못된 요청 에러 (400).
 *
 * <p>Circuit Breaker에서 무시됩니다 (클라이언트 측 문제이므로 장애가 아님). retryable=false.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceBadRequestException extends SetofCommerceException {

    public SetofCommerceBadRequestException(String responseBody) {
        super(400, responseBody, ErrorType.BAD_REQUEST);
    }
}
