package com.ryuqq.marketplace.domain.inboundqna.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** InboundQna 도메인 에러 코드. */
public enum InboundQnaErrorCode implements ErrorCode {
    INBOUND_QNA_NOT_FOUND("IBQ-001", 404, "인바운드 QnA를 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("IBQ-002", 400, "유효하지 않은 인바운드 QnA 상태 변경입니다"),
    DUPLICATE_EXTERNAL_QNA("IBQ-003", 409, "이미 수신된 외부 QnA입니다"),
    CONVERSION_FAILED("IBQ-004", 500, "내부 QnA 변환에 실패했습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    InboundQnaErrorCode(String code, int httpStatus, String message) {
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
