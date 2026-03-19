package com.ryuqq.marketplace.application.legacyconversion.dto.result;

/**
 * 레거시 주문의 내부 ID 변환 결과.
 *
 * <p>레거시 productGroupId/productId → 내부 ID로 변환한 결과를 담습니다. 매핑이 없는 경우 레거시 ID를 그대로 사용합니다.
 *
 * @param internalProductGroupId 내부 상품그룹 ID (매핑 없으면 레거시 ID)
 * @param internalProductId 내부 상품 ID (매핑 없으면 레거시 ID)
 * @param sellerName 셀러명 (legacy_seller_id_mapping에서 조회)
 * @param brandName 브랜드명 (order_snapshot_product_group에서 조회 — 현재 luxurydb에 별도 컬럼 없으므로 null 가능)
 */
public record LegacyOrderResolvedIds(
        long internalProductGroupId, long internalProductId, String sellerName, String brandName) {}
