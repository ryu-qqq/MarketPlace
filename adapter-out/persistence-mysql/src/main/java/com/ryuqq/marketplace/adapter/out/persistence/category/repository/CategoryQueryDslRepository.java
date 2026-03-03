package com.ryuqq.marketplace.adapter.out.persistence.category.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.category.condition.CategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import com.ryuqq.marketplace.domain.category.query.CategorySortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Category QueryDSL Repository. */
@Repository
public class CategoryQueryDslRepository {

    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CategoryConditionBuilder conditionBuilder;

    public CategoryQueryDslRepository(
            JPAQueryFactory queryFactory, CategoryConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<CategoryJpaEntity> findById(Long id) {
        CategoryJpaEntity entity =
                queryFactory
                        .selectFrom(category)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<CategoryJpaEntity> findByCriteria(CategorySearchCriteria criteria) {
        return queryFactory
                .selectFrom(category)
                .where(
                        conditionBuilder.parentIdEq(criteria),
                        conditionBuilder.depthEq(criteria),
                        conditionBuilder.leafEq(criteria),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.departmentIn(criteria),
                        conditionBuilder.categoryGroupIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.notDeleted())
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(CategorySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(category.count())
                        .from(category)
                        .where(
                                conditionBuilder.parentIdEq(criteria),
                                conditionBuilder.depthEq(criteria),
                                conditionBuilder.leafEq(criteria),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.departmentIn(criteria),
                                conditionBuilder.categoryGroupIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public List<CategoryJpaEntity> findAllByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(category)
                .where(category.id.in(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    public List<Long> findDescendantIds(List<String> pathPrefixes) {
        return queryFactory
                .select(category.id)
                .from(category)
                .where(
                        conditionBuilder.pathStartsWithAny(pathPrefixes),
                        conditionBuilder.notDeleted())
                .fetch();
    }

    public boolean existsByCode(String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(category)
                        .where(category.code.eq(code), conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(CategorySearchCriteria criteria) {
        CategorySortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case SORT_ORDER -> isAsc ? category.sortOrder.asc() : category.sortOrder.desc();
            case CREATED_AT -> isAsc ? category.createdAt.asc() : category.createdAt.desc();
            case NAME_KO -> isAsc ? category.nameKo.asc() : category.nameKo.desc();
            case CODE -> isAsc ? category.code.asc() : category.code.desc();
        };
    }
}
