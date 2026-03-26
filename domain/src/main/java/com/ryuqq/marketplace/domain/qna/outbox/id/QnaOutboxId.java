package com.ryuqq.marketplace.domain.qna.outbox.id;

/** QnA 아웃박스 ID Value Object. */
public record QnaOutboxId(Long value) {

    public static QnaOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("QnaOutboxId 값은 null일 수 없습니다");
        }
        return new QnaOutboxId(value);
    }

    public static QnaOutboxId forNew() {
        return new QnaOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
