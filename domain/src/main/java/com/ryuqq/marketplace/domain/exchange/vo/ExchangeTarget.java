package com.ryuqq.marketplace.domain.exchange.vo;

/** 교환 재배송 대상 상품 (우리 내부 상품 참조). */
public record ExchangeTarget(long productGroupId, long productId, String skuCode, int quantity) {

    public ExchangeTarget {
        if (skuCode == null || skuCode.isBlank()) {
            throw new IllegalArgumentException("SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
    }
}
