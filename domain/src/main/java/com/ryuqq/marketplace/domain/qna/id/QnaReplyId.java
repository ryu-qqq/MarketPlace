package com.ryuqq.marketplace.domain.qna.id;

public record QnaReplyId(Long value) {

    public static QnaReplyId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("QnaReplyId value must not be null");
        }
        return new QnaReplyId(value);
    }

    public static QnaReplyId forNew() {
        return new QnaReplyId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
