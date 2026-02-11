package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ProductGroup 도메인 에러 코드. */
public enum ProductGroupErrorCode implements ErrorCode {

    PRODUCT_GROUP_NOT_FOUND("PRDGRP-001", 404, "상품 그룹을 찾을 수 없습니다"),
    PRODUCT_GROUP_INVALID_STATUS_TRANSITION("PRDGRP-002", 400, "유효하지 않은 상태 전이입니다"),
    PRODUCT_GROUP_NO_THUMBNAIL("PRDGRP-003", 400, "대표 이미지(THUMBNAIL)가 최소 1개 필요합니다"),
    PRODUCT_GROUP_INVALID_OPTION_STRUCTURE("PRDGRP-004", 400, "옵션 타입과 옵션 그룹 구조가 일치하지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductGroupErrorCode(String code, int httpStatus, String message) {
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
