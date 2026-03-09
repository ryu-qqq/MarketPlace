package com.ryuqq.marketplace.domain.order.vo;

/**
 * 내부 상품 참조. 매핑 결과로 연결된 우리 시스템의 상품 정보입니다. 정산, 재고관리 등에 사용됩니다.
 *
 * <p>스냅샷 필드(productGroupName, brandName, sellerName, mainImageUrl)는 주문 생성 시점의 상품/브랜드/셀러 이름을 기록합니다.
 * 나중에 상품이 변경되더라도 주문 시점 정보를 유지합니다.
 *
 * @param productGroupId 상품그룹 ID (매핑 안 됐으면 null)
 * @param productId 상품 ID (매핑 안 됐으면 null)
 * @param sellerId 셀러 ID (매핑 안 됐으면 null)
 * @param brandId 브랜드 ID (매핑 안 됐으면 null)
 * @param skuCode SKU 코드 (매핑 안 됐으면 null)
 * @param productGroupName 상품그룹 이름 스냅샷 (nullable)
 * @param brandName 브랜드 이름 스냅샷 (nullable)
 * @param sellerName 셀러 이름 스냅샷 (nullable)
 * @param mainImageUrl 대표 이미지 URL 스냅샷 (nullable)
 */
public record InternalProductReference(
        Long productGroupId,
        Long productId,
        Long sellerId,
        Long brandId,
        String skuCode,
        String productGroupName,
        String brandName,
        String sellerName,
        String mainImageUrl) {

    public static InternalProductReference of(
            Long productGroupId,
            Long productId,
            Long sellerId,
            Long brandId,
            String skuCode,
            String productGroupName,
            String brandName,
            String sellerName,
            String mainImageUrl) {
        return new InternalProductReference(
                productGroupId,
                productId,
                sellerId,
                brandId,
                skuCode,
                productGroupName,
                brandName,
                sellerName,
                mainImageUrl);
    }
}
