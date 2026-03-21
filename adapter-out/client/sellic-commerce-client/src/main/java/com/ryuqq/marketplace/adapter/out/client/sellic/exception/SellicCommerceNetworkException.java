package com.ryuqq.marketplace.adapter.out.client.sellic.exception;

/** 셀릭 커머스 API 네트워크 에러 (타임아웃, 연결 실패). CB 기록, retryable=true. */
public class SellicCommerceNetworkException extends SellicCommerceException {

    public SellicCommerceNetworkException(String message, Throwable cause) {
        super(message, cause, ErrorType.NETWORK);
    }
}
