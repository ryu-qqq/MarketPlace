package com.ryuqq.marketplace.domain.cancel.outbox.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 취소 아웃박스 멱등키 VO.
 *
 * <p>형식: {@code COBO:{orderItemId}:{outboxType}:{epochMilli}}
 */
public record CancelOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "COBO";
    private static final String DELIMITER = ":";

    public static CancelOutboxIdempotencyKey generate(
            String orderItemId, CancelOutboxType outboxType, Instant createdAt) {
        Objects.requireNonNull(orderItemId, "orderItemId는 필수입니다");
        Objects.requireNonNull(outboxType, "outboxType은 필수입니다");
        Objects.requireNonNull(createdAt, "createdAt은 필수입니다");

        String value =
                PREFIX
                        + DELIMITER
                        + orderItemId
                        + DELIMITER
                        + outboxType.name()
                        + DELIMITER
                        + createdAt.toEpochMilli();
        return new CancelOutboxIdempotencyKey(value);
    }

    public static CancelOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new CancelOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
