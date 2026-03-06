package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.QCategoryPresetJpaEntity.categoryPresetJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity.shopJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite.CategoryMappingWithCategoryDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.QCategoryMappingJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** CategoryMapping QueryDSL Repository. */
@Repository
public class CategoryMappingQueryDslRepository {

    private static final QCategoryMappingJpaEntity categoryMapping =
            QCategoryMappingJpaEntity.categoryMappingJpaEntity;
    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    private final JPAQueryFactory queryFactory;

    public CategoryMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<CategoryMappingWithCategoryDto> findMappedCategoriesByPresetId(Long presetId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryMappingWithCategoryDto.class,
                                categoryMapping.id,
                                categoryMapping.internalCategoryId,
                                category.nameKo,
                                category.displayPath,
                                category.code))
                .from(categoryMapping)
                .join(category)
                .on(categoryMapping.internalCategoryId.eq(category.id))
                .where(categoryMapping.presetId.eq(presetId), categoryMapping.status.eq("ACTIVE"))
                .fetch();
    }

    /**
     * 내부 카테고리 ID → 판매채널 카테고리 ID 역조회.
     *
     * <p>CategoryMapping → CategoryPreset → Shop 3-way JOIN.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID (매핑 없으면 empty)
     */
    public Optional<Long> findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return Optional.ofNullable(
                queryFactory
                        .select(categoryMapping.salesChannelCategoryId)
                        .from(categoryMapping)
                        .join(categoryPresetJpaEntity)
                        .on(categoryMapping.presetId.eq(categoryPresetJpaEntity.id))
                        .join(shopJpaEntity)
                        .on(categoryPresetJpaEntity.shopId.eq(shopJpaEntity.id))
                        .where(
                                categoryMapping.internalCategoryId.eq(internalCategoryId),
                                shopJpaEntity.salesChannelId.eq(salesChannelId),
                                categoryMapping.status.eq("ACTIVE"),
                                categoryPresetJpaEntity.status.eq("ACTIVE"))
                        .fetchFirst());
    }
}
