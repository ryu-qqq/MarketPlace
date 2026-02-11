package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.condition.BrandConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import com.ryuqq.marketplace.domain.brand.query.BrandSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Brand QueryDSL Repository. */
@Repository
public class BrandQueryDslRepository {

    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final BrandConditionBuilder conditionBuilder;

    public BrandQueryDslRepository(
            JPAQueryFactory queryFactory, BrandConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<BrandJpaEntity> findById(Long id) {
        BrandJpaEntity entity =
                queryFactory
                        .selectFrom(brand)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<BrandJpaEntity> findByCriteria(BrandSearchCriteria criteria) {
        return queryFactory
                .selectFrom(brand)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.notDeleted())
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(BrandSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(brand.count())
                        .from(brand)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public List<BrandJpaEntity> findAllByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(brand)
                .where(brand.id.in(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    public boolean existsByCode(String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(brand)
                        .where(brand.code.eq(code), conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(BrandSearchCriteria criteria) {
        BrandSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? brand.createdAt.asc() : brand.createdAt.desc();
            case NAME_KO -> isAsc ? brand.nameKo.asc() : brand.nameKo.desc();
            case UPDATED_AT -> isAsc ? brand.updatedAt.asc() : brand.updatedAt.desc();
        };
    }
}
