package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 클라이언트 오류 예외 (HTTP 4xx, 400/429 제외).
 *
 * <p>Circuit Breaker에서 무시됩니다. 401, 403, 404, 409 등 클라이언트 측 오류이며, 재시도 대상이 아닙니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceClientException extends NaverCommerceException {

    public NaverCommerceClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody, ErrorType.CLIENT_ERROR);
    }
}
