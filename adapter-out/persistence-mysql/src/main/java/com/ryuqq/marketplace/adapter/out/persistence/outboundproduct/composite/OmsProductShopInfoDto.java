package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

/**
 * OMS 상품 샵 정보 enrichment DTO.
 *
 * @param productGroupId 상품그룹 ID
 * @param shopId 샵 ID
 * @param shopName 샵 이름 (display_name)
 */
public record OmsProductShopInfoDto(long productGroupId, long shopId, String shopName) {}
