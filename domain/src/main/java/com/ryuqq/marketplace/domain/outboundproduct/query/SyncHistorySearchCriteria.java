package com.ryuqq.marketplace.domain.outboundproduct.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;

/**
 * 연동 이력 검색 조건 Criteria.
 *
 * @param productGroupId 상품그룹 ID
 * @param shopId 샵 ID 필터 (null이면 전체)
 * @param statusFilter 상태 필터 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record SyncHistorySearchCriteria(
        long productGroupId,
        Long shopId,
        SyncStatus statusFilter,
        QueryContext<SyncHistorySortKey> queryContext) {

    public int size() {
        return queryContext.size();
    }

    public long offset() {
        return queryContext.offset();
    }

    public int page() {
        return queryContext.page();
    }

    public boolean hasShopIdFilter() {
        return shopId != null;
    }

    public boolean hasStatusFilter() {
        return statusFilter != null;
    }
}
