package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ProductGroup 도메인 에러 코드. */
public enum ProductGroupErrorCode implements ErrorCode {
    PRODUCT_GROUP_NOT_FOUND("PRDGRP-001", 404, "상품 그룹을 찾을 수 없습니다"),
    PRODUCT_GROUP_INVALID_STATUS_TRANSITION("PRDGRP-002", 400, "유효하지 않은 상태 전이입니다"),
    PRODUCT_GROUP_NO_THUMBNAIL("PRDGRP-003", 400, "대표 이미지(THUMBNAIL)가 최소 1개 필요합니다"),
    PRODUCT_GROUP_INVALID_OPTION_STRUCTURE("PRDGRP-004", 400, "옵션 타입과 옵션 그룹 구조가 일치하지 않습니다"),
    OPTION_GROUP_EMPTY_VALUES("PRDGRP-008", 400, "옵션 그룹에 최소 1개의 옵션 값이 필요합니다"),
    OPTION_GROUP_DUPLICATE_NAME("PRDGRP-009", 400, "옵션 그룹명이 중복되었습니다"),
    OPTION_VALUE_DUPLICATE_NAME("PRDGRP-010", 400, "옵션 값 이름이 중복되었습니다"),
    PRODUCT_GROUP_IMAGE_NOT_FOUND("PRDGRP-005", 404, "상품 그룹 이미지를 찾을 수 없습니다"),
    DESCRIPTION_IMAGE_NOT_FOUND("PRDGRP-006", 404, "상세설명 이미지를 찾을 수 없습니다"),
    PRODUCT_GROUP_DESCRIPTION_NOT_FOUND("PRDGRP-007", 404, "상품 그룹 상세설명을 찾을 수 없습니다"),
    PRODUCT_GROUP_OWNERSHIP_VIOLATION("PRDGRP-011", 403, "셀러 소유가 아닌 상품 그룹이 포함되어 있습니다");

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
