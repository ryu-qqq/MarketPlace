package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 API 호출 시 발생하는 기본 예외.
 *
 * <p>HTTP 상태 코드, 응답 본문, 재시도 가능 여부를 포함합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;
    private final ErrorType errorType;
    private final boolean retryable;

    public enum ErrorType {
        SERVER_ERROR(true),
        RATE_LIMIT(true),
        NETWORK(true),
        BAD_REQUEST(false),
        CLIENT(false),
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

    public SetofCommerceException(int statusCode, String responseBody, ErrorType errorType) {
        super("세토프 커머스 API 에러: statusCode=" + statusCode + ", body=" + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorType = errorType;
        this.retryable = errorType.isRetryable();
    }

    public SetofCommerceException(String message, Throwable cause, ErrorType errorType) {
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
