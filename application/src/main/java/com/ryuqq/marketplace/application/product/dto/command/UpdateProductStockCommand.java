package com.ryuqq.marketplace.application.product.dto.command;

/**
 * 상품(SKU) 재고 수정 Command.
 *
 * @param productId 상품 ID
 * @param stockQuantity 재고 수량 (0 이상)
 */
public record UpdateProductStockCommand(long productId, int stockQuantity) {}
