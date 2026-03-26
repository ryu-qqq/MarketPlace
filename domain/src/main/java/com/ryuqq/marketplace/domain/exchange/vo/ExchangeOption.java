package com.ryuqq.marketplace.domain.exchange.vo;

/** 교환 옵션 정보: 원래 상품 + 교환 대상 상품 참조. */
public record ExchangeOption(
        long originalProductId,
        String originalSkuCode,
        long targetProductGroupId,
        long targetProductId,
        String targetSkuCode,
        int quantity) {

    public ExchangeOption {
        if (originalSkuCode == null || originalSkuCode.isBlank()) {
            throw new IllegalArgumentException("원래 SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }
        if (targetSkuCode == null || targetSkuCode.isBlank()) {
            throw new IllegalArgumentException("교환 대상 SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
    }
}
