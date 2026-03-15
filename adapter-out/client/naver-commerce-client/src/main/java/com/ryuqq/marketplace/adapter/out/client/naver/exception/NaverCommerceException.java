package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 API 호출 기본 예외.
 *
 * <p>모든 네이버 커머스 관련 예외의 상위 클래스입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public NaverCommerceException(int statusCode, String responseBody) {
        super(String.format("네이버 커머스 API 오류 [%d]: %s", statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public NaverCommerceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public int statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }
}
