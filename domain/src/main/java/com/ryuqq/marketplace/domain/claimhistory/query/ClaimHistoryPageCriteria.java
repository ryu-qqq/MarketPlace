package com.ryuqq.marketplace.domain.claimhistory.query;

import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/** 클레임 이력 페이지 조회 Criteria. */
public record ClaimHistoryPageCriteria(
        Long orderItemId, ClaimType claimType, QueryContext<ClaimHistorySortKey> queryContext) {

    public ClaimHistoryPageCriteria {
        if (orderItemId == null) {
            throw new IllegalArgumentException("orderItemId must not be null");
        }
    }

    public static ClaimHistoryPageCriteria of(
            Long orderItemId, ClaimType claimType, QueryContext<ClaimHistorySortKey> queryContext) {
        return new ClaimHistoryPageCriteria(orderItemId, claimType, queryContext);
    }

    public static ClaimHistoryPageCriteria defaultOf(Long orderItemId) {
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
