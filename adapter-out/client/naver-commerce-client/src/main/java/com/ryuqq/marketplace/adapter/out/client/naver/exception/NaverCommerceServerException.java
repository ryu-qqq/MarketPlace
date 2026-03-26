package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 서버 오류 예외 (HTTP 5xx).
 *
 * <p>Circuit Breaker에서 실패로 기록됩니다. 외부 서비스 장애를 나타내며, 재시도 대상입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceServerException extends NaverCommerceException {

    public NaverCommerceServerException(int statusCode, String responseBody) {
        super(statusCode, responseBody, ErrorType.SERVER_ERROR);
    }
}
