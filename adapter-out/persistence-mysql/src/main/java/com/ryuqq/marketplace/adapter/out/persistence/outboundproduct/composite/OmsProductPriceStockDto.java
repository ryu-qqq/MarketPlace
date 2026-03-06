package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

/**
 * OMS 상품 가격/재고 enrichment DTO.
 *
 * @param productGroupId 상품그룹 ID
 * @param price 최저 가격
 * @param stock 총 재고
 */
public record OmsProductPriceStockDto(long productGroupId, int price, int stock) {}
