package com.ryuqq.marketplace.domain.shipment.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** 배송 도메인 에러 코드. */
public enum ShipmentErrorCode implements ErrorCode {
    SHIPMENT_NOT_FOUND("SHP-001", 404, "배송 정보를 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("SHP-002", 400, "유효하지 않은 배송 상태 변경입니다"),
    TRACKING_NUMBER_REQUIRED("SHP-003", 400, "송장번호는 필수입니다"),
    SHIPMENT_NOT_SHIPPED("SHP-004", 400, "운송장이 등록되지 않은 주문은 취소 거부할 수 없습니다. 먼저 운송장을 등록해주세요");

    private final String code;
    private final int httpStatus;
    private final String message;

    ShipmentErrorCode(String code, int httpStatus, String message) {
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
