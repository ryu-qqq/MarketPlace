package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.condition.ClaimHistoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.QClaimHistoryJpaEntity;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistorySortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 클레임 이력 QueryDSL Repository. */
@Repository
public class ClaimHistoryQueryDslRepository {

    private static final QClaimHistoryJpaEntity claimHistory =
            QClaimHistoryJpaEntity.claimHistoryJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ClaimHistoryConditionBuilder conditionBuilder;

    public ClaimHistoryQueryDslRepository(
            JPAQueryFactory queryFactory, ClaimHistoryConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<ClaimHistoryJpaEntity> findByClaimTypeAndClaimId(String claimType, String claimId) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(claimHistory.claimType.eq(claimType), claimHistory.claimId.eq(claimId))
                .orderBy(claimHistory.createdAt.asc())
                .fetch();
    }

    public List<ClaimHistoryJpaEntity> findByClaimTypeAndClaimIds(
            String claimType, List<String> claimIds) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(claimHistory.claimType.eq(claimType), claimHistory.claimId.in(claimIds))
                .orderBy(claimHistory.createdAt.asc())
                .fetch();
    }

    public List<ClaimHistoryJpaEntity> findByOrderItemId(String orderItemId) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(claimHistory.orderItemId.eq(orderItemId))
                .orderBy(claimHistory.createdAt.asc())
                .fetch();
    }

    public List<ClaimHistoryJpaEntity> findByCriteria(ClaimHistoryPageCriteria criteria) {
        return queryFactory
                .selectFrom(claimHistory)
                .where(
                        conditionBuilder.orderItemIdEq(criteria),
                        conditionBuilder.claimTypeEq(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ClaimHistoryPageCriteria criteria) {
        Long count =
                queryFactory
                        .select(claimHistory.count())
                        .from(claimHistory)
                        .where(
                                conditionBuilder.orderItemIdEq(criteria),
                                conditionBuilder.claimTypeEq(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ClaimHistoryPageCriteria criteria) {
        ClaimHistorySortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? claimHistory.createdAt.asc() : claimHistory.createdAt.desc();
        };
    }
}
