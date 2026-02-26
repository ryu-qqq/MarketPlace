package com.ryuqq.marketplace.domain.refundpolicy.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 환불 정책 도메인 에러 코드. */
public enum RefundPolicyErrorCode implements ErrorCode {
    REFUND_POLICY_NOT_FOUND("RFP-001", 404, "환불 정책을 찾을 수 없습니다"),
    REFUND_POLICY_NOT_FOUND_FOR_SELLER("RFP-001-1", 404, "해당 셀러의 환불 정책을 찾을 수 없습니다"),
    REFUND_POLICY_INACTIVE("RFP-002", 400, "비활성화된 환불 정책입니다"),
    REFUND_POLICY_ALREADY_DEFAULT("RFP-003", 400, "이미 기본 환불 정책으로 설정되어 있습니다"),
    INVALID_RETURN_PERIOD("RFP-005", 400, "반품 기간이 유효하지 않습니다"),
    INVALID_EXCHANGE_PERIOD("RFP-006", 400, "교환 기간이 유효하지 않습니다"),
    RETURN_PERIOD_EXPIRED("RFP-007", 400, "반품 가능 기간이 만료되었습니다"),
    EXCHANGE_PERIOD_EXPIRED("RFP-008", 400, "교환 가능 기간이 만료되었습니다"),

    // 기본 정책 관련 에러 코드
    CANNOT_DEACTIVATE_DEFAULT_POLICY("RFP-011", 400, "기본 환불 정책은 비활성화할 수 없습니다"),
    INACTIVE_POLICY_CANNOT_BE_DEFAULT("RFP-012", 400, "비활성화된 정책은 기본으로 지정할 수 없습니다"),
    CANNOT_UNMARK_ONLY_DEFAULT_POLICY("RFP-013", 400, "유일한 기본 환불 정책은 해제할 수 없습니다"),
    LAST_ACTIVE_POLICY_CANNOT_BE_DEACTIVATED("RFP-014", 400, "최소 1개의 활성 환불 정책이 필요합니다"),
    DEFAULT_REFUND_POLICY_NOT_FOUND("RFP-015", 400, "기본 환불 정책이 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    RefundPolicyErrorCode(String code, int httpStatus, String message) {
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
