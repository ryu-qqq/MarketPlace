package com.ryuqq.marketplace.domain.seller.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 셀러 도메인 에러 코드. */
public enum SellerErrorCode implements ErrorCode {

    // 셀러 관련 (SEL-001 ~ SEL-099)
    SELLER_NOT_FOUND("SEL-001", 404, "셀러를 찾을 수 없습니다"),
    SELLER_ALREADY_EXISTS("SEL-002", 409, "이미 존재하는 셀러입니다"),
    SELLER_INACTIVE("SEL-003", 400, "비활성화된 셀러입니다"),
    SELLER_ALREADY_ACTIVE("SEL-004", 400, "이미 활성화된 셀러입니다"),
    SELLER_ALREADY_INACTIVE("SEL-005", 400, "이미 비활성화된 셀러입니다"),
    SELLER_NAME_DUPLICATE("SEL-006", 409, "이미 존재하는 셀러명입니다"),

    // 사업자 정보 관련 (SEL-100 ~ SEL-199)
    BUSINESS_INFO_NOT_FOUND("SEL-100", 404, "사업자 정보를 찾을 수 없습니다"),
    BUSINESS_INFO_ALREADY_EXISTS("SEL-101", 409, "이미 사업자 정보가 등록되어 있습니다"),
    INVALID_REGISTRATION_NUMBER("SEL-102", 400, "유효하지 않은 사업자등록번호입니다"),
    REGISTRATION_NUMBER_DUPLICATE("SEL-103", 409, "이미 등록된 사업자등록번호입니다"),

    // 주소 관련 (SEL-200 ~ SEL-299)
    ADDRESS_NOT_FOUND("SEL-200", 404, "주소를 찾을 수 없습니다"),
    ADDRESS_TYPE_MISMATCH("SEL-201", 400, "주소 유형이 일치하지 않습니다"),
    DEFAULT_ADDRESS_REQUIRED("SEL-202", 400, "기본 주소는 삭제할 수 없습니다"),
    DEFAULT_ADDRESS_ALREADY_EXISTS("SEL-203", 400, "이미 기본 주소가 설정되어 있습니다"),

    // CS 정보 관련 (SEL-300 ~ SEL-399)
    CS_NOT_FOUND("SEL-300", 404, "CS 정보를 찾을 수 없습니다"),

    // 계약 정보 관련 (SEL-400 ~ SEL-499)
    CONTRACT_NOT_FOUND("SEL-400", 404, "계약 정보를 찾을 수 없습니다"),

    // 정산 정보 관련 (SEL-500 ~ SEL-599)
    SETTLEMENT_NOT_FOUND("SEL-500", 404, "정산 정보를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    SellerErrorCode(String code, int httpStatus, String message) {
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
