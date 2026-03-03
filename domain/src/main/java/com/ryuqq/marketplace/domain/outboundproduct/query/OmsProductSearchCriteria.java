package com.ryuqq.marketplace.domain.outboundproduct.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;

/**
 * OMS 상품 목록 검색 조건 Criteria.
 *
 * @param statuses 상품 그룹 상태 필터 (empty이면 전체)
 * @param syncStatuses 연동 상태 필터 (empty이면 전체)
 * @param shopIds 쇼핑몰 ID 목록 필터 (empty이면 전체)
 * @param partnerIds 파트너(셀러) ID 목록 필터 (empty이면 전체)
 * @param productCodes 상품 코드 목록 필터 (empty이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param dateRange 날짜 범위 필터 (null이면 제한 없음)
 * @param dateType 날짜 필터 대상 (CREATED_AT / UPDATED_AT)
 * @param queryContext 정렬 및 페이징 정보
 */
public record OmsProductSearchCriteria(
        List<ProductGroupStatus> statuses,
        List<SyncStatus> syncStatuses,
        List<Long> shopIds,
        List<Long> partnerIds,
        List<String> productCodes,
        OmsProductSearchField searchField,
        String searchWord,
        DateRange dateRange,
        String dateType,
        QueryContext<OmsProductSortKey> queryContext) {

    public OmsProductSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        syncStatuses = syncStatuses != null ? List.copyOf(syncStatuses) : List.of();
        shopIds = shopIds != null ? List.copyOf(shopIds) : List.of();
        partnerIds = partnerIds != null ? List.copyOf(partnerIds) : List.of();
        productCodes = productCodes != null ? List.copyOf(productCodes) : List.of();
    }

    public static OmsProductSearchCriteria defaultCriteria() {
        return new OmsProductSearchCriteria(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(OmsProductSortKey.defaultKey()));
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasSyncStatusFilter() {
        return !syncStatuses.isEmpty();
    }

    public boolean hasShopFilter() {
        return !shopIds.isEmpty();
    }

    public boolean hasPartnerFilter() {
        return !partnerIds.isEmpty();
    }

    public boolean hasProductCodeFilter() {
        return !productCodes.isEmpty();
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 특정 필드 검색인지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
    }

    public boolean hasDateRange() {
        return dateRange != null && !dateRange.isEmpty();
    }

    public int size() {
        return queryContext.size();
    }

    public long offset() {
        return queryContext.offset();
    }

    public int page() {
        return queryContext.page();
    }
}
