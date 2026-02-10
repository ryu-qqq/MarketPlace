package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.condition.SalesChannelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.QSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** SalesChannel QueryDSL Repository. */
@Repository
public class SalesChannelQueryDslRepository {

    private static final QSalesChannelJpaEntity salesChannel =
            QSalesChannelJpaEntity.salesChannelJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final SalesChannelConditionBuilder conditionBuilder;

    public SalesChannelQueryDslRepository(
            JPAQueryFactory queryFactory, SalesChannelConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SalesChannelJpaEntity> findById(Long id) {
        SalesChannelJpaEntity entity =
                queryFactory.selectFrom(salesChannel).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SalesChannelJpaEntity> findByCriteria(SalesChannelSearchCriteria criteria) {
        return queryFactory
                .selectFrom(salesChannel)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(SalesChannelSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(salesChannel.count())
                        .from(salesChannel)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsByChannelName(String channelName) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(salesChannel)
                        .where(conditionBuilder.channelNameEq(channelName))
                        .fetchFirst();
        return count != null;
    }

    public boolean existsByChannelNameExcluding(String channelName, Long excludeId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(salesChannel)
                        .where(
                                conditionBuilder.channelNameEq(channelName),
                                salesChannel.id.ne(excludeId))
                        .fetchFirst();
        return count != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(SalesChannelSearchCriteria criteria) {
        SalesChannelSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? salesChannel.createdAt.asc() : salesChannel.createdAt.desc();
            case CHANNEL_NAME ->
                    isAsc ? salesChannel.channelName.asc() : salesChannel.channelName.desc();
        };
    }
}
