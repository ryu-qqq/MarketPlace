package com.ryuqq.marketplace.domain.setofsync.id;

public record SetofSyncOutboxId(Long value) {

    public static SetofSyncOutboxId of(Long value) {
        return new SetofSyncOutboxId(value);
    }

    public static SetofSyncOutboxId forNew() {
        return new SetofSyncOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
