package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.condition.CategoryPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.QCategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.QSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** CategoryPreset QueryDSL Repository. */
@Repository
public class CategoryPresetQueryDslRepository {

    private static final QCategoryPresetJpaEntity categoryPreset =
            QCategoryPresetJpaEntity.categoryPresetJpaEntity;
    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;
    private static final QSalesChannelCategoryJpaEntity salesChannelCategory =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;
    private static final QSalesChannelJpaEntity salesChannel =
            QSalesChannelJpaEntity.salesChannelJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CategoryPresetConditionBuilder conditionBuilder;

    public CategoryPresetQueryDslRepository(
            JPAQueryFactory queryFactory, CategoryPresetConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<CategoryPresetJpaEntity> findById(Long id) {
        CategoryPresetJpaEntity entity =
                queryFactory.selectFrom(categoryPreset).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<CategoryPresetJpaEntity> findAllByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(categoryPreset)
                .where(conditionBuilder.idsIn(ids), conditionBuilder.statusActive())
                .fetch();
    }

    public List<CategoryPresetCompositeDto> findByCriteria(CategoryPresetSearchCriteria criteria) {
        return compositeQuery()
                .where(
                        conditionBuilder.salesChannelIdsIn(criteria),
                        conditionBuilder.statusesIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.createdAtGoe(criteria),
                        conditionBuilder.createdAtLoe(criteria))
                .orderBy(categoryPreset.createdAt.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(CategoryPresetSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(categoryPreset.count())
                        .from(categoryPreset)
                        .join(shop)
                        .on(categoryPreset.shopId.eq(shop.id))
                        .join(salesChannelCategory)
                        .on(categoryPreset.salesChannelCategoryId.eq(salesChannelCategory.id))
                        .where(
                                conditionBuilder.salesChannelIdsIn(criteria),
                                conditionBuilder.statusesIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.createdAtGoe(criteria),
                                conditionBuilder.createdAtLoe(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Optional<Long> findSalesChannelCategoryIdByCode(
            Long salesChannelId, String categoryCode) {
        Long id =
                queryFactory
                        .select(salesChannelCategory.id)
                        .from(salesChannelCategory)
                        .where(
                                salesChannelCategory.salesChannelId.eq(salesChannelId),
                                salesChannelCategory.externalCategoryCode.eq(categoryCode))
                        .fetchFirst();
        return Optional.ofNullable(id);
    }

    private JPAQuery<CategoryPresetCompositeDto> compositeQuery() {
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryPresetCompositeDto.class,
                                categoryPreset.id,
                                categoryPreset.shopId,
                                shop.shopName,
                                shop.accountId,
                                shop.salesChannelId,
                                salesChannel.channelName,
                                categoryPreset.salesChannelCategoryId,
                                salesChannelCategory.externalCategoryCode,
                                salesChannelCategory.displayPath,
                                categoryPreset.presetName,
                                categoryPreset.status,
                                categoryPreset.createdAt))
                .from(categoryPreset)
                .join(shop)
                .on(categoryPreset.shopId.eq(shop.id))
                .join(salesChannelCategory)
                .on(categoryPreset.salesChannelCategoryId.eq(salesChannelCategory.id))
                .join(salesChannel)
                .on(shop.salesChannelId.eq(salesChannel.id));
    }
}
