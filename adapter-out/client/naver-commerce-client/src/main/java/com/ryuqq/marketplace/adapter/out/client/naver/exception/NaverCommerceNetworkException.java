package com.ryuqq.marketplace.adapter.out.client.naver.exception;

/**
 * 네이버 커머스 네트워크 오류 예외 (타임아웃/연결실패).
 *
 * <p>Circuit Breaker에서 실패로 기록됩니다. 네트워크 지터, 타임아웃 등 일시적 장애를 나타내며, 재시도 대상입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class NaverCommerceNetworkException extends NaverCommerceException {

    public NaverCommerceNetworkException(String message, Throwable cause) {
        super(message, cause, ErrorType.NETWORK);
    }
}
