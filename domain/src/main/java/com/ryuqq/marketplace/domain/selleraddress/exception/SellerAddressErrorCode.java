package com.ryuqq.marketplace.domain.selleraddress.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 셀러 주소 도메인 에러 코드. */
public enum SellerAddressErrorCode implements ErrorCode {
    SELLER_ADDRESS_NOT_FOUND("ADDR-001", 404, "셀러 주소를 찾을 수 없습니다"),
    CANNOT_DELETE_DEFAULT_ADDRESS("ADDR-002", 400, "기본 주소는 삭제할 수 없습니다"),
    DEFAULT_ADDRESS_ALREADY_EXISTS("ADDR-003", 400, "이미 기본 주소가 설정되어 있습니다"),
    DUPLICATE_ADDRESS_NAME("ADDR-004", 400, "동일한 배송지 타입에 같은 이름의 배송지가 이미 존재합니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SellerAddressErrorCode(String code, int httpStatus, String message) {
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
