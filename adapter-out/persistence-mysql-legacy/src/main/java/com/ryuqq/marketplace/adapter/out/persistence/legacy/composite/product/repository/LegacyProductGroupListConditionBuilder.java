package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupEntity.legacyProductGroupEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 목록 조회 동적 WHERE 조건 빌더.
 *
 * <p>LegacyProductGroupSearchCriteria 기반으로 QueryDSL BooleanExpression을 생성합니다.
 */
@Component
public class LegacyProductGroupListConditionBuilder {

    /**
     * deleteYn = 'N' 기본 조건.
     *
     * @return deleteYn 조건
     */
    public BooleanExpression notDeleted() {
        return legacyProductGroupEntity.deleteYn.eq("N");
    }

    /**
     * insertDate between startDate and endDate 조건.
     *
     * @param startDate 시작일 (null이면 조건 없음)
     * @param endDate 종료일 (null이면 조건 없음)
     * @return 날짜 범위 조건 또는 null
     */
    public BooleanExpression betweenTime(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return legacyProductGroupEntity.insertDate.between(startDate, endDate);
    }

    /**
     * managementType 조건.
     *
     * @param managementType 관리유형 (null이면 조건 없음)
     * @return managementType 조건 또는 null
     */
    public BooleanExpression managementTypeEq(String managementType) {
        if (managementType == null || managementType.isBlank()) {
            return null;
        }
        return legacyProductGroupEntity.managementType.eq(managementType);
    }

    /**
     * brandId 조건.
     *
     * @param brandId 브랜드 ID (null이면 조건 없음)
     * @return brandId 조건 또는 null
     */
    public BooleanExpression brandEq(Long brandId) {
        if (brandId == null) {
            return null;
        }
        return legacyProductGroupEntity.brandId.eq(brandId);
    }

    /**
     * sellerId 조건.
     *
     * @param sellerId 판매자 ID (null이면 조건 없음)
     * @return sellerId 조건 또는 null
     */
    public BooleanExpression sellerIdEq(Long sellerId) {
        if (sellerId == null) {
            return null;
        }
        return legacyProductGroupEntity.sellerId.eq(sellerId);
    }

    /**
     * soldOutYn 조건.
     *
     * @param soldOutYn 품절 여부 Y/N (null이면 조건 없음)
     * @return soldOutYn 조건 또는 null
     */
    public BooleanExpression soldOutEq(String soldOutYn) {
        if (soldOutYn == null || soldOutYn.isBlank()) {
            return null;
        }
        return legacyProductGroupEntity.soldOutYn.eq(soldOutYn);
    }

    /**
     * displayYn 조건.
     *
     * @param displayYn 노출 여부 Y/N (null이면 조건 없음)
     * @return displayYn 조건 또는 null
     */
    public BooleanExpression displayEq(String displayYn) {
        if (displayYn == null || displayYn.isBlank()) {
            return null;
        }
        return legacyProductGroupEntity.displayYn.eq(displayYn);
    }

    /**
     * categoryId IN 조건.
     *
     * @param categoryIds 카테고리 ID 목록 (empty이면 조건 없음)
     * @return categoryId IN 조건 또는 null
     */
    public BooleanExpression categoryIn(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return legacyProductGroupEntity.categoryId.in(categoryIds);
    }

    /**
     * salePrice between minSalePrice and maxSalePrice 조건.
     *
     * @param minSalePrice 최소 판매가 (null이면 하한 없음)
     * @param maxSalePrice 최대 판매가 (null이면 상한 없음)
     * @return salePrice 범위 조건 또는 null
     */
    public BooleanExpression betweenPrice(Long minSalePrice, Long maxSalePrice) {
        if (minSalePrice == null && maxSalePrice == null) {
            return null;
        }
        if (minSalePrice == null) {
            return legacyProductGroupEntity.salePrice.loe(maxSalePrice);
        }
        if (maxSalePrice == null) {
            return legacyProductGroupEntity.salePrice.goe(minSalePrice);
        }
        return legacyProductGroupEntity.salePrice.between(minSalePrice, maxSalePrice);
    }

    /**
     * discountRate between minDiscountRate and maxDiscountRate 조건.
     *
     * @param minDiscountRate 최소 할인율 (null이면 하한 없음)
     * @param maxDiscountRate 최대 할인율 (null이면 상한 없음)
     * @return discountRate 범위 조건 또는 null
     */
    public BooleanExpression betweenSalePercent(Long minDiscountRate, Long maxDiscountRate) {
        if (minDiscountRate == null && maxDiscountRate == null) {
            return null;
        }
        if (minDiscountRate == null) {
            return legacyProductGroupEntity.discountRate.loe(maxDiscountRate.intValue());
        }
        if (maxDiscountRate == null) {
            return legacyProductGroupEntity.discountRate.goe(minDiscountRate.intValue());
        }
        return legacyProductGroupEntity.discountRate.between(
                minDiscountRate.intValue(), maxDiscountRate.intValue());
    }

    /**
     * 동적 검색 조건 (searchKeyword 기반).
     *
     * <p>PRODUCT_GROUP_NAME: productGroupName LIKE %searchWord%. PRODUCT_GROUP_ID: id = searchWord.
     * 기타/null: productGroupName LIKE %searchWord%
     *
     * @param searchKeyword 검색 유형 (null이면 조건 없음)
     * @param searchWord 검색어 (null이면 조건 없음)
     * @return 동적 검색 조건 또는 null
     */
    public BooleanExpression searchKeywordEq(String searchKeyword, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if ("PRODUCT_GROUP_ID".equalsIgnoreCase(searchKeyword)) {
            try {
                long id = Long.parseLong(searchWord.trim());
                return legacyProductGroupEntity.id.eq(id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return legacyProductGroupEntity.productGroupName.contains(searchWord);
    }

    /**
     * 검색 조건 배열을 반환합니다 (QueryFactory.where()에 직접 전달).
     *
     * @param criteria 검색 조건
     * @return BooleanExpression 배열 (null 요소 포함 가능)
     */
    public BooleanExpression[] buildConditions(LegacyProductGroupSearchCriteria criteria) {
        return new BooleanExpression[] {
            notDeleted(),
            betweenTime(criteria.startDate(), criteria.endDate()),
            managementTypeEq(criteria.managementType()),
            brandEq(criteria.brandId()),
            sellerIdEq(criteria.sellerId()),
            soldOutEq(criteria.soldOutYn()),
            displayEq(criteria.displayYn()),
            categoryIn(criteria.categoryIds()),
            betweenPrice(criteria.minSalePrice(), criteria.maxSalePrice()),
            betweenSalePercent(criteria.minDiscountRate(), criteria.maxDiscountRate()),
            searchKeywordEq(criteria.searchKeyword(), criteria.searchWord())
        };
    }
}
