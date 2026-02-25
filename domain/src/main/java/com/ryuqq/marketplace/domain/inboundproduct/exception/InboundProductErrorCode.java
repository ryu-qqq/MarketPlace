package com.ryuqq.marketplace.domain.inboundproduct.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** InboundProduct 도메인 에러 코드. */
public enum InboundProductErrorCode implements ErrorCode {
    INBOUND_PRODUCT_NOT_FOUND("IBP-001", 404, "인바운드 상품을 찾을 수 없습니다"),
    INBOUND_PRODUCT_INVALID_STATUS("IBP-002", 400, "인바운드 상품 상태가 올바르지 않습니다"),
    INBOUND_PRODUCT_MAPPING_FAILED("IBP-003", 400, "인바운드 상품 매핑에 실패했습니다"),
    INBOUND_PRODUCT_CONVERSION_FAILED("IBP-004", 500, "인바운드 상품 변환에 실패했습니다"),
    INBOUND_PRODUCT_VALIDATION_FAILED("IBP-005", 400, "인바운드 상품 검증에 실패했습니다"),
    INBOUND_PRODUCT_ITEM_NOT_FOUND("IBP-006", 404, "인바운드 상품 아이템을 찾을 수 없습니다"),
    INBOUND_PRODUCT_ITEM_UNMAPPED("IBP-007", 400, "매핑되지 않은 외부 상품 ID가 포함되어 있습니다"),
    INBOUND_PRODUCT_PAYLOAD_INVALID("IBP-008", 400, "인바운드 상품 페이로드가 유효하지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    InboundProductErrorCode(String code, int httpStatus, String message) {
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
