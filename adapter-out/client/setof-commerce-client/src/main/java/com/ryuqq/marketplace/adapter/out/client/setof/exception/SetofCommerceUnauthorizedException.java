package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 인증 실패 에러 (401).
 *
 * <p>토큰 만료 시 발생합니다. Circuit Breaker와 Retry에서 무시되며, 어댑터에서 토큰 재발급 후 재시도합니다. retryable=false.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceUnauthorizedException extends SetofCommerceException {

    public SetofCommerceUnauthorizedException(String responseBody) {
        super(401, responseBody, ErrorType.CLIENT);
    }
}
