package com.ryuqq.marketplace.domain.exchange.outbox.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 교환 아웃박스 멱등키 VO.
 *
 * <p>형식: {@code EXBO:{orderItemId}:{outboxType}:{epochMilli}}
 */
public record ExchangeOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "EXBO";
    private static final String DELIMITER = ":";

    public static ExchangeOutboxIdempotencyKey generate(
            String orderItemId, ExchangeOutboxType outboxType, Instant createdAt) {
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
        return new ExchangeOutboxIdempotencyKey(value);
    }

    public static ExchangeOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new ExchangeOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
