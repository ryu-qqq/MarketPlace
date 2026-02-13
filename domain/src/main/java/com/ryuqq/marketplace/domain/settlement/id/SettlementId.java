package com.ryuqq.marketplace.domain.settlement.id;

/** 정산 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record SettlementId(String value) {

    public static SettlementId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SettlementId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new SettlementId(value);
    }

    public static SettlementId forNew(String value) {
        return of(value);
    }
}
