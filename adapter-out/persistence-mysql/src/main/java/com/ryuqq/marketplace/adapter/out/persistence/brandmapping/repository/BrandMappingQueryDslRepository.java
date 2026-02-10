package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.condition.BrandMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.QBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** BrandMapping QueryDSL Repository. */
@Repository
public class BrandMappingQueryDslRepository {

    private static final QBrandMappingJpaEntity brandMapping =
            QBrandMappingJpaEntity.brandMappingJpaEntity;
    private static final QSalesChannelBrandJpaEntity salesChannelBrand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;
    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final BrandMappingConditionBuilder conditionBuilder;

    public BrandMappingQueryDslRepository(
            JPAQueryFactory queryFactory, BrandMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<BrandMappingJpaEntity> findById(Long id) {
        BrandMappingJpaEntity entity =
                queryFactory.selectFrom(brandMapping).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<BrandMappingJpaEntity> findByCriteria(BrandMappingSearchCriteria criteria) {
        JPAQuery<BrandMappingJpaEntity> query = queryFactory.selectFrom(brandMapping);

        applyJoinsIfNeeded(query, criteria);

        return query.where(
                        conditionBuilder.salesChannelBrandIdsIn(criteria),
                        conditionBuilder.internalBrandIdsIn(criteria),
                        conditionBuilder.salesChannelIdsIn(criteria),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(BrandMappingSearchCriteria criteria) {
        JPAQuery<Long> query = queryFactory.select(brandMapping.count()).from(brandMapping);

        applyJoinsIfNeeded(query, criteria);

        Long count =
                query.where(
                                conditionBuilder.salesChannelBrandIdsIn(criteria),
                                conditionBuilder.internalBrandIdsIn(criteria),
                                conditionBuilder.salesChannelIdsIn(criteria),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsBySalesChannelBrandId(Long salesChannelBrandId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(brandMapping)
                        .where(brandMapping.salesChannelBrandId.eq(salesChannelBrandId))
                        .fetchFirst();
        return count != null;
    }

    private <T> void applyJoinsIfNeeded(JPAQuery<T> query, BrandMappingSearchCriteria criteria) {
        if (conditionBuilder.needsSalesChannelBrandJoin(criteria)) {
            query.join(salesChannelBrand)
                    .on(brandMapping.salesChannelBrandId.eq(salesChannelBrand.id));
        }
        if (conditionBuilder.needsBrandJoin(criteria)) {
            query.join(brand).on(brandMapping.internalBrandId.eq(brand.id));
        }
    }

    private OrderSpecifier<?> resolveOrderSpecifier(BrandMappingSearchCriteria criteria) {
        BrandMappingSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? brandMapping.createdAt.asc() : brandMapping.createdAt.desc();
        };
    }
}
