package com.ryuqq.marketplace.domain.qna.id;

public record QnaId(Long value) {

    public static QnaId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("QnaId value must not be null");
        }
        return new QnaId(value);
    }

    public static QnaId forNew() {
        return new QnaId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
