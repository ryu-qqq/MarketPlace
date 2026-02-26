package com.ryuqq.marketplace.domain.exchange.id;

/** 교환 대상 상품 ID. 영속화 전에는 null입니다. */
public record ExchangeItemId(Long value) {

    public static ExchangeItemId of(long value) {
        return new ExchangeItemId(value);
    }

    public static ExchangeItemId forNew() {
        return new ExchangeItemId(null);
    }
}
