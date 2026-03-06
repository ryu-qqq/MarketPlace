package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

/**
 * OMS 상품 대표 이미지 enrichment DTO.
 *
 * @param productGroupId 상품그룹 ID
 * @param imageUrl 대표 이미지 URL
 */
public record OmsProductMainImageDto(long productGroupId, String imageUrl) {}
