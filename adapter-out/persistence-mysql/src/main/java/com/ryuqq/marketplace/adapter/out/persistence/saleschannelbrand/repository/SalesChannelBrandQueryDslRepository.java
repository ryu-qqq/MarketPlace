package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.condition.SalesChannelBrandConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** SalesChannelBrand QueryDSL Repository. */
@Repository
public class SalesChannelBrandQueryDslRepository {

    private static final QSalesChannelBrandJpaEntity brand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final SalesChannelBrandConditionBuilder conditionBuilder;

    public SalesChannelBrandQueryDslRepository(
            JPAQueryFactory queryFactory, SalesChannelBrandConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SalesChannelBrandJpaEntity> findById(Long id) {
        SalesChannelBrandJpaEntity entity =
                queryFactory.selectFrom(brand).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SalesChannelBrandJpaEntity> findByCriteria(
            SalesChannelBrandSearchCriteria criteria) {
        return queryFactory
                .selectFrom(brand)
                .where(
                        conditionBuilder.salesChannelIdsIn(criteria.salesChannelIds()),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(SalesChannelBrandSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(brand.count())
                        .from(brand)
                        .where(
                                conditionBuilder.salesChannelIdsIn(criteria.salesChannelIds()),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalBrandCode) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(brand)
                        .where(
                                conditionBuilder.salesChannelIdEq(salesChannelId),
                                conditionBuilder.externalBrandCodeEq(externalBrandCode))
                        .fetchFirst();
        return count != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(SalesChannelBrandSearchCriteria criteria) {
        SalesChannelBrandSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? brand.createdAt.asc() : brand.createdAt.desc();
            case EXTERNAL_NAME ->
                    isAsc ? brand.externalBrandName.asc() : brand.externalBrandName.desc();
        };
    }
}
