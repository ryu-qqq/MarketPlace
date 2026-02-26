package com.ryuqq.marketplace.domain.shippingpolicy.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 배송 정책 도메인 에러 코드. */
public enum ShippingPolicyErrorCode implements ErrorCode {
    SHIPPING_POLICY_NOT_FOUND("SHP-001", 404, "배송 정책을 찾을 수 없습니다"),
    SHIPPING_POLICY_NOT_FOUND_FOR_SELLER("SHP-001-1", 404, "해당 셀러의 배송 정책을 찾을 수 없습니다"),
    SHIPPING_POLICY_INACTIVE("SHP-002", 400, "비활성화된 배송 정책입니다"),
    SHIPPING_POLICY_ALREADY_DEFAULT("SHP-003", 400, "이미 기본 배송 정책으로 설정되어 있습니다"),
    INVALID_FREE_THRESHOLD("SHP-005", 400, "무료배송 기준금액이 유효하지 않습니다"),

    // 기본 정책 관련 에러 코드
    CANNOT_DEACTIVATE_DEFAULT_POLICY("SHP-011", 400, "기본 배송 정책은 비활성화할 수 없습니다"),
    INACTIVE_POLICY_CANNOT_BE_DEFAULT("SHP-012", 400, "비활성화된 정책은 기본으로 지정할 수 없습니다"),
    CANNOT_UNMARK_ONLY_DEFAULT_POLICY("SHP-013", 400, "유일한 기본 배송 정책은 해제할 수 없습니다"),
    LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED("SHP-014", 400, "최소 1개의 활성 배송 정책이 필요합니다"),
    DEFAULT_SHIPPING_POLICY_NOT_FOUND("SHP-015", 400, "기본 배송 정책이 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ShippingPolicyErrorCode(String code, int httpStatus, String message) {
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
