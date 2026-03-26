package com.ryuqq.marketplace.adapter.out.client.sellic.exception;

/** 셀릭 커머스 API 400 Bad Request. CB 무시, retryable=false. */
public class SellicCommerceBadRequestException extends SellicCommerceException {

    public SellicCommerceBadRequestException(String responseBody) {
        super(400, responseBody, ErrorType.BAD_REQUEST);
    }
}
