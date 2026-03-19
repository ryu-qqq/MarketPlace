package com.ryuqq.marketplace.adapter.out.persistence.exchange.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.condition.ExchangeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeClaimJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSortKey;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 교환 클레임 QueryDSL Repository. */
@Repository
public class ExchangeClaimQueryDslRepository {

    private static final QExchangeClaimJpaEntity exchangeClaim =
            QExchangeClaimJpaEntity.exchangeClaimJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ExchangeConditionBuilder conditionBuilder;

    public ExchangeClaimQueryDslRepository(
            JPAQueryFactory queryFactory, ExchangeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ExchangeClaimJpaEntity> findById(String id) {
        ExchangeClaimJpaEntity entity =
                queryFactory.selectFrom(exchangeClaim).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ExchangeClaimJpaEntity> findByOrderItemId(String orderItemId) {
        ExchangeClaimJpaEntity entity =
                queryFactory
                        .selectFrom(exchangeClaim)
                        .where(conditionBuilder.orderItemIdEq(orderItemId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExchangeClaimJpaEntity> findByOrderItemIds(List<String> orderItemIds) {
        return queryFactory
                .selectFrom(exchangeClaim)
                .where(conditionBuilder.orderItemIdIn(orderItemIds))
                .fetch();
    }

    public List<ExchangeClaimJpaEntity> findByIdIn(List<String> ids, Long sellerId) {
        return queryFactory
                .selectFrom(exchangeClaim)
                .where(conditionBuilder.idIn(ids), conditionBuilder.sellerIdEq(sellerId))
                .fetch();
    }

    public List<ExchangeClaimJpaEntity> findByCriteria(ExchangeSearchCriteria criteria) {
        return queryFactory
                .selectFrom(exchangeClaim)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRange(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ExchangeSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(exchangeClaim.count())
                        .from(exchangeClaim)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRange(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Map<ExchangeStatus, Long> countByStatus() {
        List<Tuple> results =
                queryFactory
                        .select(exchangeClaim.exchangeStatus, exchangeClaim.count())
                        .from(exchangeClaim)
                        .groupBy(exchangeClaim.exchangeStatus)
                        .fetch();

        Map<ExchangeStatus, Long> statusCounts = new EnumMap<>(ExchangeStatus.class);
        for (Tuple tuple : results) {
            String statusName = tuple.get(exchangeClaim.exchangeStatus);
            Long count = tuple.get(exchangeClaim.count());
            if (statusName != null && count != null) {
                statusCounts.put(ExchangeStatus.valueOf(statusName), count);
            }
        }
        return statusCounts;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ExchangeSearchCriteria criteria) {
        ExchangeSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc ? exchangeClaim.createdAt.asc() : exchangeClaim.createdAt.desc();
            case REQUESTED_AT ->
                    isAsc ? exchangeClaim.requestedAt.asc() : exchangeClaim.requestedAt.desc();
            case COMPLETED_AT ->
                    isAsc ? exchangeClaim.completedAt.asc() : exchangeClaim.completedAt.desc();
        };
    }
}
