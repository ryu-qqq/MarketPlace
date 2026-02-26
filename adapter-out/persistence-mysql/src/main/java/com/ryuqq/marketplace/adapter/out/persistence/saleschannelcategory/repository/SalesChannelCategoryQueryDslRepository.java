package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.condition.SalesChannelCategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** SalesChannelCategory QueryDSL Repository. */
@Repository
public class SalesChannelCategoryQueryDslRepository {

    private static final QSalesChannelCategoryJpaEntity category =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final SalesChannelCategoryConditionBuilder conditionBuilder;

    public SalesChannelCategoryQueryDslRepository(
            JPAQueryFactory queryFactory, SalesChannelCategoryConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SalesChannelCategoryJpaEntity> findById(Long id) {
        SalesChannelCategoryJpaEntity entity =
                queryFactory.selectFrom(category).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SalesChannelCategoryJpaEntity> findByCriteria(
            SalesChannelCategorySearchCriteria criteria) {
        return queryFactory
                .selectFrom(category)
                .where(
                        conditionBuilder.salesChannelIdsIn(criteria.salesChannelIds()),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(SalesChannelCategorySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(category.count())
                        .from(category)
                        .where(
                                conditionBuilder.salesChannelIdsIn(criteria.salesChannelIds()),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public List<SalesChannelCategoryJpaEntity> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(category)
                .where(category.id.in(ids))
                .orderBy(category.depth.asc())
                .fetch();
    }

    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalCategoryCode) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(category)
                        .where(
                                conditionBuilder.salesChannelIdEq(salesChannelId),
                                conditionBuilder.externalCategoryCodeEq(externalCategoryCode))
                        .fetchFirst();
        return count != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(SalesChannelCategorySearchCriteria criteria) {
        SalesChannelCategorySortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? category.createdAt.asc() : category.createdAt.desc();
            case EXTERNAL_NAME ->
                    isAsc
                            ? category.externalCategoryName.asc()
                            : category.externalCategoryName.desc();
            case SORT_ORDER -> isAsc ? category.sortOrder.asc() : category.sortOrder.desc();
        };
    }
}
