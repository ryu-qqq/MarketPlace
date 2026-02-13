package com.ryuqq.marketplace.application.product.dto.command;

/**
 * 상품(SKU) 가격 수정 Command.
 *
 * <p>discountRate는 Application 레이어에서 계산합니다. salePrice == currentPrice이면 할인 없음으로 판단합니다.
 *
 * @param productId 상품 ID
 * @param regularPrice 정가
 * @param currentPrice 판매가 (regularPrice 이하)
 * @param salePrice 할인가 (currentPrice와 같으면 할인 없음)
 */
public record UpdateProductPriceCommand(
        long productId, int regularPrice, int currentPrice, int salePrice) {}
