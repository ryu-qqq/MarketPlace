package com.ryuqq.marketplace.application.productgroup.dto.composite;

import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 목록용 Composite 결과 DTO.
 *
 * <p>ProductGroup + Brand + Category + Seller 크로스 도메인 JOIN 결과와 가격 요약, 옵션 요약 등의 배치 enrichment 데이터를
 * 포함합니다.
 *
 * <p>Adapter 레이어에서 {@link #ofBase} 팩터리로 기본 데이터를 생성하고, ReadFacade에서 {@link #withEnrichment}로 가격/옵션
 * enrichment를 적용합니다.
 */
public record ProductGroupListCompositeResult(
        Long id,
        Long sellerId,
        String sellerName,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        String categoryDisplayPath,
        String categoryIdPath,
        int categoryDepth,
        String department,
        String categoryGroup,
        String productGroupName,
        String optionType,
        String status,
        String thumbnailUrl,
        int productCount,
        int minPrice,
        int maxPrice,
        int maxDiscountRate,
        List<OptionGroupSummaryResult> optionGroups,
        Instant createdAt,
        Instant updatedAt) {

    public ProductGroupListCompositeResult {
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
    }

    /** Adapter 레이어에서 기본 Composition 쿼리 결과를 생성합니다. 가격/옵션 enrichment 필드는 기본값으로 설정됩니다. */
    public static ProductGroupListCompositeResult ofBase(
            Long id,
            Long sellerId,
            String sellerName,
            Long brandId,
            String brandName,
            Long categoryId,
            String categoryName,
            String categoryDisplayPath,
            String categoryIdPath,
            int categoryDepth,
            String department,
            String categoryGroup,
            String productGroupName,
            String optionType,
            String status,
            String thumbnailUrl,
            int productCount,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductGroupListCompositeResult(
                id,
                sellerId,
                sellerName,
                brandId,
                brandName,
                categoryId,
                categoryName,
                categoryDisplayPath,
                categoryIdPath,
                categoryDepth,
                department,
                categoryGroup,
                productGroupName,
                optionType,
                status,
                thumbnailUrl,
                productCount,
                0,
                0,
                0,
                List.of(),
                createdAt,
                updatedAt);
    }

    /**
     * 가격/옵션 enrichment 데이터를 적용한 새 인스턴스를 생성합니다.
     *
     * @param minPrice 최저 실판매가
     * @param maxPrice 최고 실판매가
     * @param maxDiscountRate 최대 할인율
     * @param optionGroups 옵션 그룹 요약 목록 (예: [{색상, [블랙, 화이트]}, {사이즈, [S, M, L]}])
     */
    public ProductGroupListCompositeResult withEnrichment(
            int minPrice,
            int maxPrice,
            int maxDiscountRate,
            List<OptionGroupSummaryResult> optionGroups) {
        return new ProductGroupListCompositeResult(
                id,
                sellerId,
                sellerName,
                brandId,
                brandName,
                categoryId,
                categoryName,
                categoryDisplayPath,
                categoryIdPath,
                categoryDepth,
                department,
                categoryGroup,
                productGroupName,
                optionType,
                status,
                thumbnailUrl,
                productCount,
                minPrice,
                maxPrice,
                maxDiscountRate,
                optionGroups,
                createdAt,
                updatedAt);
    }
}
