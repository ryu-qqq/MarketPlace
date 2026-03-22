package com.ryuqq.marketplace.domain.qna.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** QnA 도메인 에러 코드. */
public enum QnaErrorCode implements ErrorCode {
    QNA_NOT_FOUND("QNA-001", 404, "QnA를 찾을 수 없습니다"),
    INVALID_STATUS_TRANSITION("QNA-002", 400, "유효하지 않은 QnA 상태 변경입니다"),
    ALREADY_ANSWERED("QNA-003", 409, "이미 답변된 QnA입니다"),
    ALREADY_CLOSED("QNA-004", 409, "이미 종결된 QnA입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    QnaErrorCode(String code, int httpStatus, String message) {
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
