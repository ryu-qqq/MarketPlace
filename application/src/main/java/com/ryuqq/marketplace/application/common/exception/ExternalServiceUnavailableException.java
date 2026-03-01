package com.ryuqq.marketplace.application.common.exception;

/**
 * 외부 서비스 일시적 장애 예외.
 *
 * <p>Circuit Breaker OPEN 등 외부 서비스가 일시적으로 사용 불가능할 때 발생합니다. 이 예외가 발생하면 retry 횟수를 소진하지 않고 PENDING으로
 * 복귀(deferRetry)하여 서비스 복구 후 재처리합니다.
 *
 * <p>resilience4j 등 특정 라이브러리에 의존하지 않으므로 application 모듈에서 안전하게 사용 가능합니다.
 */
public class ExternalServiceUnavailableException extends RuntimeException {

    public ExternalServiceUnavailableException(String message) {
        super(message);
    }

    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
