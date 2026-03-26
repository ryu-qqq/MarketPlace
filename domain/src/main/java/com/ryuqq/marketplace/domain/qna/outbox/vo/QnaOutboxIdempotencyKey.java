package com.ryuqq.marketplace.domain.qna.outbox.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * QnA 아웃박스 멱등키 VO.
 *
 * <p>형식: {@code QNBO:{qnaId}:{outboxType}:{epochMilli}}
 */
public record QnaOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "QNBO";
    private static final String DELIMITER = ":";

    public static QnaOutboxIdempotencyKey generate(
            Long qnaId, QnaOutboxType outboxType, Instant createdAt) {
        Objects.requireNonNull(qnaId, "qnaId는 필수입니다");
        Objects.requireNonNull(outboxType, "outboxType은 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value = PREFIX + DELIMITER + qnaId + DELIMITER
                + outboxType.name() + DELIMITER + createdAt.toEpochMilli();
        return new QnaOutboxIdempotencyKey(value);
    }

    public static QnaOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new QnaOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
