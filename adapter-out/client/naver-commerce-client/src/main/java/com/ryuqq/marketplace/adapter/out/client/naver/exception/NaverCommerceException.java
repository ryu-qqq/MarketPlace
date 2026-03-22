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
    private final ErrorType errorType;
    private final boolean retryable;

    public enum ErrorType {
        SERVER_ERROR(true),
        RATE_LIMIT(true),
        NETWORK(true),
        BAD_REQUEST(false),
        CLIENT_ERROR(false),
        CIRCUIT_OPEN(false),
        UNKNOWN(false);

        private final boolean retryable;

        ErrorType(boolean retryable) {
            this.retryable = retryable;
        }

        public boolean isRetryable() {
            return retryable;
        }
    }

    public NaverCommerceException(int statusCode, String responseBody, ErrorType errorType) {
        super(String.format("네이버 커머스 API 오류 [%d]: %s", statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorType = errorType;
        this.retryable = errorType.isRetryable();
    }

    public NaverCommerceException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
        this.errorType = errorType;
        this.retryable = errorType.isRetryable();
    }

    public int statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }

    public ErrorType errorType() {
        return errorType;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
