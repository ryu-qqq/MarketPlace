package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.QClaimHistoryJpaEntity;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import org.springframework.stereotype.Component;

/** ClaimHistory QueryDSL 조건 빌더. */
@Component
public class ClaimHistoryConditionBuilder {

    private static final QClaimHistoryJpaEntity claimHistory =
            QClaimHistoryJpaEntity.claimHistoryJpaEntity;

    public BooleanExpression orderItemIdEq(ClaimHistoryPageCriteria criteria) {
        return claimHistory.orderItemId.eq(criteria.orderItemId());
    }

    public BooleanExpression claimTypeEq(ClaimHistoryPageCriteria criteria) {
        if (!criteria.hasClaimTypeFilter()) {
            return null;
        }
        return claimHistory.claimType.eq(criteria.claimType().name());
    }
}
