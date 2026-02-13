package com.ryuqq.marketplace.domain.exchange.aggregate;

import com.ryuqq.marketplace.domain.exchange.id.ExchangeItemId;

/** 교환 대상 주문 상품 (반품 대상). ExchangeClaim Aggregate 내부 구성 요소. */
public class ExchangeItem {

    private final ExchangeItemId id;
    private final long orderItemId;
    private final int exchangeQty;

    private ExchangeItem(ExchangeItemId id, long orderItemId, int exchangeQty) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.exchangeQty = exchangeQty;
    }

    public static ExchangeItem forNew(long orderItemId, int exchangeQty) {
        if (exchangeQty <= 0) {
            throw new IllegalArgumentException("교환 수량은 1 이상이어야 합니다");
        }
        return new ExchangeItem(ExchangeItemId.forNew(), orderItemId, exchangeQty);
    }

    public static ExchangeItem reconstitute(ExchangeItemId id, long orderItemId, int exchangeQty) {
        return new ExchangeItem(id, orderItemId, exchangeQty);
    }

    public ExchangeItemId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long orderItemId() {
        return orderItemId;
    }

    public int exchangeQty() {
        return exchangeQty;
    }
}
