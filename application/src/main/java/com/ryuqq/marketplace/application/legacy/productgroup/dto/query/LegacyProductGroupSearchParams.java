package com.ryuqq.marketplace.application.legacy.productgroup.dto.query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 상품그룹 목록 조회 파라미터.
 *
 * <p>Adapter-In에서 전달받은 검색 조건을 담는 Application 레이어 DTO입니다.
 * 카테고리 확장 전 원본 categoryId와 확장 후 categoryIds를 모두 보유합니다.
 *
 * @param sellerId 판매자 ID (null이면 전체)
 * @param brandId 브랜드 ID (null이면 전체)
 * @param categoryId 카테고리 ID 원본 (null이면 전체)
 * @param categoryIds 확장된 카테고리 ID 목록
 * @param managementType 관리유형 (null이면 전체)
 * @param soldOutYn 품절 여부 Y/N (null이면 전체)
 * @param displayYn 노출 여부 Y/N (null이면 전체)
 * @param minSalePrice 최소 판매가 (null이면 제한 없음)
 * @param maxSalePrice 최대 판매가 (null이면 제한 없음)
 * @param minDiscountRate 최소 할인율 (null이면 제한 없음)
 * @param maxDiscountRate 최대 할인율 (null이면 제한 없음)
 * @param searchKeyword 검색 유형 (null이면 전체)
 * @param searchWord 검색어 (null이면 전체)
 * @param startDate 조회 시작일 (null이면 제한 없음)
 * @param endDate 조회 종료일 (null이면 제한 없음)
 * @param page 페이지 번호 (0-based)
 * @param size 페이지 크기
 */
public record LegacyProductGroupSearchParams(
        Long sellerId,
        Long brandId,
        Long categoryId,
        List<Long> categoryIds,
        String managementType,
        String soldOutYn,
        String displayYn,
        Long minSalePrice,
        Long maxSalePrice,
        Long minDiscountRate,
        Long maxDiscountRate,
        String searchKeyword,
        String searchWord,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int page,
        int size) {

    public static LegacyProductGroupSearchParams of(
            Long sellerId,
            Long brandId,
            Long categoryId,
            String managementType,
            String soldOutYn,
            String displayYn,
            Long minSalePrice,
            Long maxSalePrice,
            Long minDiscountRate,
            Long maxDiscountRate,
            String searchKeyword,
            String searchWord,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        List<Long> initialCategoryIds =
                categoryId != null ? List.of(categoryId) : List.of();
        return new LegacyProductGroupSearchParams(
                sellerId,
                brandId,
                categoryId,
                initialCategoryIds,
                managementType,
                soldOutYn,
                displayYn,
                minSalePrice,
                maxSalePrice,
                minDiscountRate,
                maxDiscountRate,
                searchKeyword,
                searchWord,
                startDate,
                endDate,
                page,
                size);
    }

    /**
     * 카테고리 ID 목록을 교체한 새 인스턴스를 반환합니다.
     *
     * <p>CategoryReadManager.expandWithDescendants() 결과를 반영하기 위해 사용합니다.
     *
     * @param expandedCategoryIds 확장된 하위 카테고리 ID 목록
     * @return 카테고리 ID가 교체된 새 LegacyProductGroupSearchParams 인스턴스
     */
    public LegacyProductGroupSearchParams withCategoryIds(List<Long> expandedCategoryIds) {
        return new LegacyProductGroupSearchParams(
                sellerId,
                brandId,
                categoryId,
                expandedCategoryIds != null ? List.copyOf(expandedCategoryIds) : List.of(),
                managementType,
                soldOutYn,
                displayYn,
                minSalePrice,
                maxSalePrice,
                minDiscountRate,
                maxDiscountRate,
                searchKeyword,
                searchWord,
                startDate,
                endDate,
                page,
                size);
    }
}
