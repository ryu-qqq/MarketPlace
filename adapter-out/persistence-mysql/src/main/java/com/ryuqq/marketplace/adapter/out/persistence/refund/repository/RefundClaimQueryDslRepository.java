package com.ryuqq.marketplace.adapter.out.persistence.refund.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.refund.condition.RefundConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.query.RefundSortKey;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 환불 클레임 QueryDSL Repository. */
@Repository
public class RefundClaimQueryDslRepository {

    private static final QRefundClaimJpaEntity refundClaim =
            QRefundClaimJpaEntity.refundClaimJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final RefundConditionBuilder conditionBuilder;

    public RefundClaimQueryDslRepository(
            JPAQueryFactory queryFactory, RefundConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<RefundClaimJpaEntity> findById(String id) {
        RefundClaimJpaEntity entity =
                queryFactory.selectFrom(refundClaim).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<RefundClaimJpaEntity> findByOrderItemId(String orderItemId) {
        RefundClaimJpaEntity entity =
                queryFactory
                        .selectFrom(refundClaim)
                        .where(conditionBuilder.orderItemIdEq(orderItemId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<RefundClaimJpaEntity> findByOrderItemIds(List<String> orderItemIds) {
        return queryFactory
                .selectFrom(refundClaim)
                .where(conditionBuilder.orderItemIdIn(orderItemIds))
                .fetch();
    }

    public List<RefundClaimJpaEntity> findByIdIn(List<String> ids, Long sellerId) {
        return queryFactory
                .selectFrom(refundClaim)
                .where(conditionBuilder.idIn(ids), conditionBuilder.sellerIdEq(sellerId))
                .fetch();
    }

    public List<RefundClaimJpaEntity> findByCriteria(RefundSearchCriteria criteria) {
        return queryFactory
                .selectFrom(refundClaim)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.holdFilter(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRange(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(RefundSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(refundClaim.count())
                        .from(refundClaim)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.holdFilter(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRange(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Map<RefundStatus, Long> countByStatus() {
        List<Tuple> results =
                queryFactory
                        .select(refundClaim.refundStatus, refundClaim.count())
                        .from(refundClaim)
                        .groupBy(refundClaim.refundStatus)
                        .fetch();

        Map<RefundStatus, Long> statusCounts = new EnumMap<>(RefundStatus.class);
        for (Tuple tuple : results) {
            String statusName = tuple.get(refundClaim.refundStatus);
            Long count = tuple.get(refundClaim.count());
            if (statusName != null && count != null) {
                statusCounts.put(RefundStatus.valueOf(statusName), count);
            }
        }
        return statusCounts;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(RefundSearchCriteria criteria) {
        RefundSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? refundClaim.createdAt.asc() : refundClaim.createdAt.desc();
            case REQUESTED_AT -> isAsc ? refundClaim.requestedAt.asc() : refundClaim.requestedAt.desc();
            case COMPLETED_AT -> isAsc ? refundClaim.completedAt.asc() : refundClaim.completedAt.desc();
        };
    }
}
