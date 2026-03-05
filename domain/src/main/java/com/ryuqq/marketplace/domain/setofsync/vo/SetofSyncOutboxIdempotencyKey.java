package com.ryuqq.marketplace.domain.setofsync.vo;

import java.time.Instant;

public record SetofSyncOutboxIdempotencyKey(String value) {

    private static final String PREFIX = "SSO";

    public static SetofSyncOutboxIdempotencyKey generate(
            SetofSyncEntityType entityType, Long entityId, Instant now) {
        String key = PREFIX + ":" + entityType.name() + ":" + entityId + ":" + now.toEpochMilli();
        return new SetofSyncOutboxIdempotencyKey(key);
    }

    public static SetofSyncOutboxIdempotencyKey of(String value) {
        return new SetofSyncOutboxIdempotencyKey(value);
    }
}
