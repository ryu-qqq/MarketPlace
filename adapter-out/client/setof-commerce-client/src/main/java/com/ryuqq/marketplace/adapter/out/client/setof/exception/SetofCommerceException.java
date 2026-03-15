package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 API 호출 시 발생하는 기본 예외.
 *
 * <p>HTTP 상태 코드와 응답 본문을 포함합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public SetofCommerceException(int statusCode, String responseBody) {
        super("세토프 커머스 API 에러: statusCode=" + statusCode + ", body=" + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public SetofCommerceException(String message, Throwable cause) {
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
