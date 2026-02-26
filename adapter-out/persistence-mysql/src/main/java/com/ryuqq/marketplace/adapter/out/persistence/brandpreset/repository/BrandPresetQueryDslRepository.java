package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.condition.BrandPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.QBrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.QSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** BrandPreset QueryDSL Repository. */
@Repository
public class BrandPresetQueryDslRepository {

    private static final QBrandPresetJpaEntity brandPreset =
            QBrandPresetJpaEntity.brandPresetJpaEntity;
    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;
    private static final QSalesChannelJpaEntity salesChannel =
            QSalesChannelJpaEntity.salesChannelJpaEntity;
    private static final QSalesChannelBrandJpaEntity salesChannelBrand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final BrandPresetConditionBuilder conditionBuilder;

    public BrandPresetQueryDslRepository(
            JPAQueryFactory queryFactory, BrandPresetConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<BrandPresetJpaEntity> findById(Long id) {
        BrandPresetJpaEntity entity =
                queryFactory.selectFrom(brandPreset).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<BrandPresetJpaEntity> findAllByIds(List<Long> ids) {
        return queryFactory.selectFrom(brandPreset).where(conditionBuilder.idsIn(ids)).fetch();
    }

    public List<BrandPresetCompositeDto> findByCriteria(BrandPresetSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                BrandPresetCompositeDto.class,
                                brandPreset.id,
                                brandPreset.shopId,
                                shop.shopName,
                                shop.accountId,
                                salesChannelBrand.salesChannelId,
                                salesChannel.channelName,
                                brandPreset.salesChannelBrandId,
                                salesChannelBrand.externalBrandCode,
                                salesChannelBrand.externalBrandName,
                                brandPreset.presetName,
                                brandPreset.status,
                                brandPreset.createdAt))
                .from(brandPreset)
                .join(shop)
                .on(brandPreset.shopId.eq(shop.id))
                .join(salesChannelBrand)
                .on(brandPreset.salesChannelBrandId.eq(salesChannelBrand.id))
                .join(salesChannel)
                .on(salesChannelBrand.salesChannelId.eq(salesChannel.id))
                .where(
                        conditionBuilder.salesChannelIdsIn(criteria),
                        conditionBuilder.statusesIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.createdAtGoe(criteria),
                        conditionBuilder.createdAtLoe(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(BrandPresetSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(brandPreset.count())
                        .from(brandPreset)
                        .join(shop)
                        .on(brandPreset.shopId.eq(shop.id))
                        .join(salesChannelBrand)
                        .on(brandPreset.salesChannelBrandId.eq(salesChannelBrand.id))
                        .where(
                                conditionBuilder.salesChannelIdsIn(criteria),
                                conditionBuilder.statusesIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.createdAtGoe(criteria),
                                conditionBuilder.createdAtLoe(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Optional<BrandPresetDetailCompositeDto> findDetailCompositeById(Long id) {
        BrandPresetDetailCompositeDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        BrandPresetDetailCompositeDto.class,
                                        brandPreset.id,
                                        brandPreset.shopId,
                                        shop.shopName,
                                        shop.accountId,
                                        salesChannelBrand.salesChannelId,
                                        salesChannel.channelName,
                                        brandPreset.salesChannelBrandId,
                                        salesChannelBrand.externalBrandCode,
                                        salesChannelBrand.externalBrandName,
                                        brandPreset.presetName,
                                        brandPreset.status,
                                        brandPreset.createdAt,
                                        brandPreset.updatedAt))
                        .from(brandPreset)
                        .join(shop)
                        .on(brandPreset.shopId.eq(shop.id))
                        .join(salesChannelBrand)
                        .on(brandPreset.salesChannelBrandId.eq(salesChannelBrand.id))
                        .join(salesChannel)
                        .on(salesChannelBrand.salesChannelId.eq(salesChannel.id))
                        .where(conditionBuilder.idEq(id))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public Optional<Long> findSalesChannelIdBySalesChannelBrandId(Long salesChannelBrandId) {
        Long salesChannelId =
                queryFactory
                        .select(salesChannelBrand.salesChannelId)
                        .from(salesChannelBrand)
                        .where(salesChannelBrand.id.eq(salesChannelBrandId))
                        .fetchOne();
        return Optional.ofNullable(salesChannelId);
    }

    private OrderSpecifier<?> resolveOrderSpecifier(BrandPresetSearchCriteria criteria) {
        BrandPresetSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? brandPreset.createdAt.asc() : brandPreset.createdAt.desc();
        };
    }
}
