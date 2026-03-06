package com.ryuqq.marketplace.domain.outboundseller.vo;

import java.time.Instant;

public record OutboundSellerOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "OSO";

    public static OutboundSellerOutboxIdempotencyKey generate(
            OutboundSellerEntityType entityType, Long entityId, Instant now) {
        String key = PREFIX + ":" + entityType.name() + ":" + entityId + ":" + now.toEpochMilli();
        return new OutboundSellerOutboxIdempotencyKey(key);
    }

    public static OutboundSellerOutboxIdempotencyKey of(String value) {
        return new OutboundSellerOutboxIdempotencyKey(value);
    }
}
