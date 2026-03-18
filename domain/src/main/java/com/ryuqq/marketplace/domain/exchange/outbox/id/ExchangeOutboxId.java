package com.ryuqq.marketplace.domain.exchange.outbox.id;

/** 교환 아웃박스 ID Value Object. */
public record ExchangeOutboxId(Long value) {

    public static ExchangeOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExchangeOutboxId 값은 null일 수 없습니다");
        }
        return new ExchangeOutboxId(value);
    }

    public static ExchangeOutboxId forNew() {
        return new ExchangeOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
