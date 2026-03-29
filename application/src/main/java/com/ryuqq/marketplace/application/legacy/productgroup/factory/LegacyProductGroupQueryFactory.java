package com.ryuqq.marketplace.application.legacy.productgroup.factory;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyBrandIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyCategoryIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacySellerIdResolver;
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
 * <p>레거시 검색 파라미터를 표준 검색 파라미터로 변환합니다. sellerId/brandId/categoryId를 internal ID로 resolve합니다.
 */
@Component
public class LegacyProductGroupQueryFactory {

    private final LegacySellerIdResolver sellerIdResolver;
    private final LegacyBrandIdResolver brandIdResolver;
    private final LegacyCategoryIdResolver categoryIdResolver;

    public LegacyProductGroupQueryFactory(
            LegacySellerIdResolver sellerIdResolver,
            LegacyBrandIdResolver brandIdResolver,
            LegacyCategoryIdResolver categoryIdResolver) {
        this.sellerIdResolver = sellerIdResolver;
        this.brandIdResolver = brandIdResolver;
        this.categoryIdResolver = categoryIdResolver;
    }

    /** 레거시 검색 파라미터를 레거시 검색 Criteria로 변환합니다 (luxurydb 직접 조회용, 하위 호환). */
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
     * <p>sellerId/brandId/categoryId를 internal ID로 resolve합니다.
     */
    public ProductGroupSearchParams toStandardSearchParams(LegacyProductGroupSearchParams params) {
        List<String> statuses = resolveStatuses(params.soldOutYn(), params.displayYn());

        // sellerId는 LegacyAccessChecker에서 이미 internal ID로 resolve됨
        List<Long> sellerIds = params.sellerId() != null ? List.of(params.sellerId()) : List.of();

        List<Long> brandIds =
                params.brandId() != null
                        ? List.of(brandIdResolver.resolve(params.brandId()))
                        : List.of();

        List<Long> categoryIds =
                params.categoryIds() != null
                        ? params.categoryIds().stream().map(categoryIdResolver::resolve).toList()
                        : List.of();

        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        toLocalDate(params.startDate()),
                        toLocalDate(params.endDate()),
                        "id",
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
            statuses.add("DRAFT");
        }
        return statuses;
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}
