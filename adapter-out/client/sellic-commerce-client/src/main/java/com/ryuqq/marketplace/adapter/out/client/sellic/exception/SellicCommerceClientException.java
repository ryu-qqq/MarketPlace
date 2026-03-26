package com.ryuqq.marketplace.adapter.out.client.sellic.exception;

/** 셀릭 커머스 API 4xx 클라이언트 에러 (400, 429 제외). CB 무시, retryable=false. */
public class SellicCommerceClientException extends SellicCommerceException {

    public SellicCommerceClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody, ErrorType.CLIENT);
    }
}
