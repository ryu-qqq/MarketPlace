package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 잘못된 요청 예외 (HTTP 400).
 *
 * <p>Circuit Breaker에서 무시됩니다. 우리 측 데이터 오류이므로 외부 서비스 장애가 아닙니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceBadRequestException extends NaverCommerceException {

    public NaverCommerceBadRequestException(String responseBody) {
        super(400, responseBody);
    }
}
