package com.ryuqq.marketplace.application.product.dto.command;

/**
 * 상품(SKU) 가격 수정 Command.
 *
 * <p>salePrice와 discountRate는 도메인 내부에서 자동 계산됩니다.
 *
 * @param productId 상품 ID
 * @param regularPrice 정가
 * @param currentPrice 판매가 (regularPrice 이하)
 */
public record UpdateProductPriceCommand(long productId, int regularPrice, int currentPrice) {}
