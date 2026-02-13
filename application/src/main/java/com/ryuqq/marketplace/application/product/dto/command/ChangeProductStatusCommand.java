package com.ryuqq.marketplace.application.product.dto.command;

/**
 * 상품(SKU) 상태 변경 Command.
 *
 * @param productId 상품 ID
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLDOUT")
 */
public record ChangeProductStatusCommand(long productId, String targetStatus) {}
