package com.ryuqq.marketplace.domain.qna.vo;

/**
 * QnA 상태 머신.
 *
 * <p>PENDING → ANSWERED → CLOSED. ANSWERED 상태에서만 CLOSED로 전이 가능합니다.
 */
public enum QnaStatus {
    /** 문의 등록, 미답변 */
    PENDING,

    /** 판매자 답변 완료 */
    ANSWERED,

    /** 종결 */
    CLOSED;

    public boolean canAnswer() {
        return this == PENDING;
    }

    public boolean canClose() {
        return this == ANSWERED;
    }

    public boolean isTerminal() {
        return this == CLOSED;
    }
}
