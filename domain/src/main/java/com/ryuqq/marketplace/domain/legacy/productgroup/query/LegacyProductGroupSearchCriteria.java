package com.ryuqq.marketplace.domain.legacy.productgroup.query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 상품그룹 검색 조건 Criteria.
 *
 * <p>QueryFactory에서 변환된 최종 검색 조건입니다. luxurydb 상품그룹 목록 조회 시 사용합니다.
 *
 * @param sellerId 판매자 ID (null이면 전체)
 * @param brandId 브랜드 ID (null이면 전체)
 * @param categoryIds 카테고리 ID 목록 (empty이면 전체)
 * @param managementType 관리유형 (null이면 전체)
 * @param soldOutYn 품절 여부 Y/N (null이면 전체)
 * @param displayYn 노출 여부 Y/N (null이면 전체)
 * @param minSalePrice 최소 판매가 (null이면 제한 없음)
 * @param maxSalePrice 최대 판매가 (null이면 제한 없음)
 * @param minDiscountRate 최소 할인율 (null이면 제한 없음)
 * @param maxDiscountRate 최대 할인율 (null이면 제한 없음)
 * @param searchKeyword 검색 유형 (PRODUCT_GROUP_NAME, PRODUCT_GROUP_ID 등, null이면 전체)
 * @param searchWord 검색어 (null이면 전체)
 * @param startDate 조회 시작일 (null이면 제한 없음)
 * @param endDate 조회 종료일 (null이면 제한 없음)
 * @param page 페이지 번호 (0-based)
 * @param size 페이지 크기
 */
public record LegacyProductGroupSearchCriteria(
        Long sellerId,
        Long brandId,
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

    public LegacyProductGroupSearchCriteria {
        categoryIds = categoryIds != null ? List.copyOf(categoryIds) : List.of();
    }

    public static LegacyProductGroupSearchCriteria of(
            Long sellerId,
            Long brandId,
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
        return new LegacyProductGroupSearchCriteria(
                sellerId,
                brandId,
                categoryIds,
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

    /** 오프셋 반환 (편의 메서드). */
    public long offset() {
        return (long) page * size;
    }

    /** 카테고리 필터가 있는지 확인. */
    public boolean hasCategoryFilter() {
        return !categoryIds.isEmpty();
    }

    /** 날짜 범위 필터가 있는지 확인. */
    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 가격 범위 필터가 있는지 확인. */
    public boolean hasPriceRange() {
        return minSalePrice != null || maxSalePrice != null;
    }

    /** 할인율 범위 필터가 있는지 확인. */
    public boolean hasDiscountRateRange() {
        return minDiscountRate != null || maxDiscountRate != null;
    }
}
