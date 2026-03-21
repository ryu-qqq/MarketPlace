package com.ryuqq.marketplace.adapter.out.client.sellic.exception;

/** 셀릭 커머스 API 5xx 서버 에러. CB 기록, retryable=true. */
public class SellicCommerceServerException extends SellicCommerceException {

    public SellicCommerceServerException(int statusCode, String responseBody) {
        super(statusCode, responseBody, ErrorType.SERVER_ERROR);
    }
}
