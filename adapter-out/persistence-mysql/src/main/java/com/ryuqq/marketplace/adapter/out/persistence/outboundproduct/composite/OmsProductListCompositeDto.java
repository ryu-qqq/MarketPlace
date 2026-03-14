package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

import java.time.Instant;

/**
 * OMS 상품 목록 Composite 조회 DTO.
 *
 * <p>outbound_products JOIN product_groups JOIN shops LEFT JOIN sellers LEFT JOIN brands 결과.
 *
 * @param productGroupId 상품그룹 ID
 * @param productGroupName 상품명
 * @param status 상품 상태
 * @param sellerId 셀러 ID
 * @param sellerName 셀러명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명(nameKo)
 * @param shopId 샵 ID
 * @param shopName 샵 이름
 * @param externalProductId 외부 채널 상품 ID (nullable)
 * @param createdAt 등록일
 * @param updatedAt 수정일
 */
public record OmsProductListCompositeDto(
        Long productGroupId,
        String productGroupName,
        String status,
        Long sellerId,
        String sellerName,
        Long brandId,
        String brandName,
        Long shopId,
        String shopName,
        String externalProductId,
        Instant createdAt,
        Instant updatedAt) {}
