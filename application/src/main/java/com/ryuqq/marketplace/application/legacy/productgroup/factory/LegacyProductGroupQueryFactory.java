package com.ryuqq.marketplace.application.legacy.productgroup.factory;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 레거시 검색 파라미터를 표준 ProductGroupSearchParams로 변환합니다 (market 스키마 조회용).
     *
     * @param params 레거시 검색 파라미터
     * @return 표준 검색 파라미터
     */
    public ProductGroupSearchParams toStandardSearchParams(
            LegacyProductGroupSearchParams params) {
        List<String> statuses = resolveStatuses(params.soldOutYn(), params.displayYn());
        List<Long> sellerIds = params.sellerId() != null ? List.of(params.sellerId()) : List.of();
        List<Long> brandIds = params.brandId() != null ? List.of(params.brandId()) : List.of();
        List<Long> categoryIds = params.categoryIds() != null ? params.categoryIds() : List.of();

        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        toLocalDate(params.startDate()),
                        toLocalDate(params.endDate()),
                        "createdAt",
                        "DESC",
                        params.page(),
                        params.size());

        return ProductGroupSearchParams.of(
                statuses,
                sellerIds,
                brandIds,
                categoryIds,
                List.of(),
                params.searchKeyword(),
                params.searchWord(),
                commonParams);
    }

    private List<String> resolveStatuses(String soldOutYn, String displayYn) {
        List<String> statuses = new ArrayList<>();
        if ("Y".equals(soldOutYn)) {
            statuses.add("SOLD_OUT");
        }
        if ("N".equals(displayYn)) {
            statuses.add("INACTIVE");
        }
        if ("Y".equals(displayYn)) {
            statuses.add("ACTIVE");
        }
        return statuses;
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}
