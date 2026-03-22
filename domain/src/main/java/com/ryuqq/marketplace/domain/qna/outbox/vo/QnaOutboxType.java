package com.ryuqq.marketplace.domain.qna.outbox.vo;

/** QnA 아웃박스 유형. 외부 채널에 동기화해야 하는 QnA 상태 변경 유형. */
public enum QnaOutboxType {

    /** 판매자 답변 등록 */
    ANSWER("답변 등록");

    private final String description;

    QnaOutboxType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
