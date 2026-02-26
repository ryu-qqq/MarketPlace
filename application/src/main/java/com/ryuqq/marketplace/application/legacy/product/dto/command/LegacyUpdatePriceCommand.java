package com.ryuqq.marketplace.application.legacy.product.dto.command;

/**
 * 레거시 상품 가격 수정 Command.
 *
 * @param productGroupId 레거시 상품그룹 PK
 * @param regularPrice 정가
 * @param currentPrice 판매가
 */
public record LegacyUpdatePriceCommand(long productGroupId, long regularPrice, long currentPrice) {}
