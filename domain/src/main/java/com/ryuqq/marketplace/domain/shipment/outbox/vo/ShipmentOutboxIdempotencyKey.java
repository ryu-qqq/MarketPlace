package com.ryuqq.marketplace.domain.shipment.outbox.vo;

import java.time.Instant;
import java.util.Objects;

/**
 * 배송 아웃박스 멱등키 VO.
 *
 * <p>형식: {@code SHPO:{orderItemId}:{outboxType}:{epochMilli}}
 */
public record ShipmentOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "SHPO";
    private static final String DELIMITER = ":";

    public static ShipmentOutboxIdempotencyKey generate(
            Long orderItemId, ShipmentOutboxType outboxType, Instant createdAt) {
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
        return new ShipmentOutboxIdempotencyKey(value);
    }

    public static ShipmentOutboxIdempotencyKey of(String value) {
        Objects.requireNonNull(value, "멱등키 값은 필수입니다");
        if (!value.startsWith(PREFIX + DELIMITER)) {
            throw new IllegalArgumentException("잘못된 멱등키 형식입니다: " + value);
        }
        return new ShipmentOutboxIdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
