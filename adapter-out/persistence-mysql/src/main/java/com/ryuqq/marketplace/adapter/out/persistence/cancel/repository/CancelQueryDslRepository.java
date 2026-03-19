package com.ryuqq.marketplace.adapter.out.persistence.cancel.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.condition.CancelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.query.CancelSortKey;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Cancel QueryDSL Repository. */
@Repository
public class CancelQueryDslRepository {

    private static final QCancelJpaEntity cancel = QCancelJpaEntity.cancelJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CancelConditionBuilder conditionBuilder;

    public CancelQueryDslRepository(
            JPAQueryFactory queryFactory, CancelConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<CancelJpaEntity> findByIdIn(List<String> ids, Long sellerId) {
        return queryFactory
                .selectFrom(cancel)
                .where(conditionBuilder.idIn(ids), conditionBuilder.sellerIdEq(sellerId))
                .fetch();
    }

    public Optional<CancelJpaEntity> findById(String id) {
        CancelJpaEntity entity =
                queryFactory.selectFrom(cancel).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<CancelJpaEntity> findByOrderItemId(String orderItemId) {
        CancelJpaEntity entity =
                queryFactory
                        .selectFrom(cancel)
                        .where(conditionBuilder.orderItemIdEq(orderItemId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<CancelJpaEntity> findByOrderItemIds(List<String> orderItemIds) {
        return queryFactory
                .selectFrom(cancel)
                .where(conditionBuilder.orderItemIdIn(orderItemIds))
                .fetch();
    }

    public List<CancelJpaEntity> findByCriteria(CancelSearchCriteria criteria) {
        return queryFactory
                .selectFrom(cancel)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.typeIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRange(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(CancelSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(cancel.count())
                        .from(cancel)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.typeIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRange(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Map<CancelStatus, Long> countByStatus() {
        List<Tuple> results =
                queryFactory
                        .select(cancel.cancelStatus, cancel.count())
                        .from(cancel)
                        .groupBy(cancel.cancelStatus)
                        .fetch();

        Map<CancelStatus, Long> statusCounts = new EnumMap<>(CancelStatus.class);
        for (Tuple tuple : results) {
            String statusName = tuple.get(cancel.cancelStatus);
            Long count = tuple.get(cancel.count());
            if (statusName != null && count != null) {
                statusCounts.put(CancelStatus.valueOf(statusName), count);
            }
        }
        return statusCounts;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(CancelSearchCriteria criteria) {
        CancelSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? cancel.createdAt.asc() : cancel.createdAt.desc();
            case REQUESTED_AT -> isAsc ? cancel.requestedAt.asc() : cancel.requestedAt.desc();
            case COMPLETED_AT -> isAsc ? cancel.completedAt.asc() : cancel.completedAt.desc();
        };
    }
}
