package com.ryuqq.marketplace.application.product.dto.command;

import java.util.List;

/**
 * 상품(SKU) 배치 가격/재고 수정 Command.
 *
 * @param sellerId 셀러 ID (소유권 검증용)
 * @param entries 수정할 상품 항목 목록
 */
public record BatchUpdateProductCommand(Long sellerId, List<Entry> entries) {

    /**
     * 개별 상품 수정 항목.
     *
     * @param productId 상품 ID
     * @param regularPrice 정가
     * @param currentPrice 판매가
     * @param stockQuantity 재고 수량
     */
    public record Entry(long productId, int regularPrice, int currentPrice, int stockQuantity) {}
}
