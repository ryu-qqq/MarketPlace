package com.ryuqq.marketplace.domain.exchange.id;

/** 교환 클레임 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record ExchangeClaimId(String value) {

    public static ExchangeClaimId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExchangeClaimId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ExchangeClaimId(value);
    }

    public static ExchangeClaimId forNew(String value) {
        return of(value);
    }
}
