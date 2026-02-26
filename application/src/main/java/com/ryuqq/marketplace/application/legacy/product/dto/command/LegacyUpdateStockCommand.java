package com.ryuqq.marketplace.application.legacy.product.dto.command;

import java.util.List;

/**
 * 레거시 상품 재고 수정 Command.
 *
 * @param productGroupId 세토프 상품그룹 PK
 * @param stockEntries 재고 수정 엔트리 목록
 */
public record LegacyUpdateStockCommand(long productGroupId, List<StockEntry> stockEntries) {

    /** 하나의 재고 수정 엔트리. */
    public record StockEntry(long productId, int stockQuantity) {}
}
