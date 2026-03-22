package com.ryuqq.marketplace.adapter.out.client.setof.exception;

/**
 * 세토프 커머스 네트워크 에러 (타임아웃 / 연결 실패).
 *
 * <p>Circuit Breaker에 실패로 기록됩니다. 네트워크 문제이므로 재시도 가치 있음. retryable=true.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class SetofCommerceNetworkException extends SetofCommerceException {

    public SetofCommerceNetworkException(String message, Throwable cause) {
        super(message, cause, ErrorType.NETWORK);
    }
}
