package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.condition.CategoryMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.QCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** CategoryMapping QueryDSL Repository. */
@Repository
public class CategoryMappingQueryDslRepository {

    private static final QCategoryMappingJpaEntity categoryMapping =
            QCategoryMappingJpaEntity.categoryMappingJpaEntity;
    private static final QSalesChannelCategoryJpaEntity salesChannelCategory =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;
    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CategoryMappingConditionBuilder conditionBuilder;

    public CategoryMappingQueryDslRepository(
            JPAQueryFactory queryFactory, CategoryMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<CategoryMappingJpaEntity> findById(Long id) {
        CategoryMappingJpaEntity entity =
                queryFactory
                        .selectFrom(categoryMapping)
                        .where(conditionBuilder.idEq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<CategoryMappingJpaEntity> findByCriteria(CategoryMappingSearchCriteria criteria) {
        JPAQuery<CategoryMappingJpaEntity> query = queryFactory.selectFrom(categoryMapping);

        applyJoinsIfNeeded(query, criteria);

        return query.where(
                        conditionBuilder.salesChannelCategoryIdsIn(criteria),
                        conditionBuilder.internalCategoryIdsIn(criteria),
                        conditionBuilder.salesChannelIdsIn(criteria),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(CategoryMappingSearchCriteria criteria) {
        JPAQuery<Long> query = queryFactory.select(categoryMapping.count()).from(categoryMapping);

        applyJoinsIfNeeded(query, criteria);

        Long count =
                query.where(
                                conditionBuilder.salesChannelCategoryIdsIn(criteria),
                                conditionBuilder.internalCategoryIdsIn(criteria),
                                conditionBuilder.salesChannelIdsIn(criteria),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsBySalesChannelCategoryId(Long salesChannelCategoryId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(categoryMapping)
                        .where(categoryMapping.salesChannelCategoryId.eq(salesChannelCategoryId))
                        .fetchFirst();
        return count != null;
    }

    private <T> void applyJoinsIfNeeded(JPAQuery<T> query, CategoryMappingSearchCriteria criteria) {
        if (conditionBuilder.needsSalesChannelCategoryJoin(criteria)) {
            query.join(salesChannelCategory)
                    .on(categoryMapping.salesChannelCategoryId.eq(salesChannelCategory.id));
        }
        if (conditionBuilder.needsCategoryJoin(criteria)) {
            query.join(category).on(categoryMapping.internalCategoryId.eq(category.id));
        }
    }

    private OrderSpecifier<?> resolveOrderSpecifier(CategoryMappingSearchCriteria criteria) {
        CategoryMappingSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc ? categoryMapping.createdAt.asc() : categoryMapping.createdAt.desc();
        };
    }
}
