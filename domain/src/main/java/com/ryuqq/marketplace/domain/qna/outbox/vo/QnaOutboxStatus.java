package com.ryuqq.marketplace.domain.qna.outbox.vo;

/** QnA 아웃박스 처리 상태. */
@SuppressWarnings("PMD.DataClass")
public enum QnaOutboxStatus {
    PENDING("대기"),
    PROCESSING("처리중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    QnaOutboxStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPending() { return this == PENDING; }
    public boolean isProcessing() { return this == PROCESSING; }
    public boolean isCompleted() { return this == COMPLETED; }
    public boolean isFailed() { return this == FAILED; }
    public boolean canProcess() { return this == PENDING; }
    public boolean isTerminal() { return this == COMPLETED || this == FAILED; }
}
