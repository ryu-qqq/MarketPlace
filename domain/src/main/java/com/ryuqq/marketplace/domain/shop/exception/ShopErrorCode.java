package com.ryuqq.marketplace.domain.shop.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** Shop 도메인 에러 코드. */
public enum ShopErrorCode implements ErrorCode {
    SHOP_NOT_FOUND("SHP-001", 404, "외부몰을 찾을 수 없습니다"),
    SHOP_NAME_DUPLICATE("SHP-002", 409, "이미 존재하는 외부몰명입니다"),
    SHOP_ACCOUNT_ID_DUPLICATE("SHP-003", 409, "이미 존재하는 외부몰 계정 ID입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ShopErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
