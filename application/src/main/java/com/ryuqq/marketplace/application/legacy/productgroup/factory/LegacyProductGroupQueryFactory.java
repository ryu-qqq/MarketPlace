package com.ryuqq.marketplace.application.legacy.productgroup.factory;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 QueryFactory.
 *
 * <p>LegacyProductGroupSearchParams → LegacyProductGroupSearchCriteria 변환을 담당합니다.
 */
@Component
public class LegacyProductGroupQueryFactory {

    /**
     * 검색 파라미터를 검색 Criteria로 변환합니다.
     *
     * @param params 검색 파라미터 (카테고리 확장 완료 상태)
     * @return 검색 Criteria
     */
    public LegacyProductGroupSearchCriteria createCriteria(LegacyProductGroupSearchParams params) {
        return LegacyProductGroupSearchCriteria.of(
                params.sellerId(),
                params.brandId(),
                params.categoryIds(),
                params.managementType(),
                params.soldOutYn(),
                params.displayYn(),
                params.minSalePrice(),
                params.maxSalePrice(),
                params.minDiscountRate(),
                params.maxDiscountRate(),
                params.searchKeyword(),
                params.searchWord(),
                params.startDate(),
                params.endDate(),
                params.page(),
                params.size());
    }
}
