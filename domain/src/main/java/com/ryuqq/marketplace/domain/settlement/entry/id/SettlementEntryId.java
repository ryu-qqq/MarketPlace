package com.ryuqq.marketplace.domain.settlement.entry.id;

import java.util.UUID;

/** 정산 원장 ID (UUIDv7 기반). */
public record SettlementEntryId(String value) {

    public SettlementEntryId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SettlementEntryId는 비어 있을 수 없습니다");
        }
    }

    public static SettlementEntryId of(String value) {
        return new SettlementEntryId(value);
    }

    public static SettlementEntryId forNew(String value) {
        return new SettlementEntryId(value);
    }

    public static SettlementEntryId generate() {
        return new SettlementEntryId(UUID.randomUUID().toString());
    }
}
