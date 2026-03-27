package com.ryuqq.marketplace.domain.claimhistory.query;

import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/** 클레임 이력 페이지 조회 Criteria. */
public record ClaimHistoryPageCriteria(
        String orderItemId, ClaimType claimType, QueryContext<ClaimHistorySortKey> queryContext) {

    public ClaimHistoryPageCriteria {
        if (orderItemId == null || orderItemId.isBlank()) {
            throw new IllegalArgumentException("orderItemId must not be null or blank");
        }
    }

    public static ClaimHistoryPageCriteria of(
            String orderItemId,
            ClaimType claimType,
            QueryContext<ClaimHistorySortKey> queryContext) {
        return new ClaimHistoryPageCriteria(orderItemId, claimType, queryContext);
    }

    public static ClaimHistoryPageCriteria defaultOf(String orderItemId) {
        return new ClaimHistoryPageCriteria(
                orderItemId, null, QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));
    }

    public boolean hasClaimTypeFilter() {
        return claimType != null;
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
