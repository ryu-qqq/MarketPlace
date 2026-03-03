package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.QBrandMappingJpaEntity.brandMappingJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.QBrandPresetJpaEntity.brandPresetJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.QCategoryMappingJpaEntity.categoryMappingJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.QCategoryPresetJpaEntity.categoryPresetJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity.shopJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

/**
 * 아웃바운드 매핑 역조회 QueryDSL 레포지토리.
 *
 * <p>내부 카테고리/브랜드 ID → 판매채널 카테고리/브랜드 ID 변환 쿼리를 담당합니다.
 */
@Repository
public class OutboundMappingQueryDslRepository {

    private static final String STATUS_ACTIVE = "ACTIVE";

    private final JPAQueryFactory queryFactory;

    public OutboundMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 내부 카테고리 ID → 판매채널 카테고리 ID 역조회.
     *
     * <p>CategoryMapping → CategoryPreset → Shop 3-way JOIN.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID (없으면 null)
     */
    public Long findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return queryFactory
                .select(categoryMappingJpaEntity.salesChannelCategoryId)
                .from(categoryMappingJpaEntity)
                .join(categoryPresetJpaEntity)
                .on(categoryMappingJpaEntity.presetId.eq(categoryPresetJpaEntity.id))
                .join(shopJpaEntity)
                .on(categoryPresetJpaEntity.shopId.eq(shopJpaEntity.id))
                .where(
                        categoryMappingJpaEntity.internalCategoryId.eq(internalCategoryId),
                        shopJpaEntity.salesChannelId.eq(salesChannelId),
                        categoryMappingJpaEntity.status.eq(STATUS_ACTIVE),
                        categoryPresetJpaEntity.status.eq(STATUS_ACTIVE))
                .fetchFirst();
    }

    /**
     * 내부 브랜드 ID → 판매채널 브랜드 ID 역조회.
     *
     * <p>BrandMapping → BrandPreset → Shop 3-way JOIN.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID (없으면 null)
     */
    public Long findSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return queryFactory
                .select(brandMappingJpaEntity.salesChannelBrandId)
                .from(brandMappingJpaEntity)
                .join(brandPresetJpaEntity)
                .on(brandMappingJpaEntity.presetId.eq(brandPresetJpaEntity.id))
                .join(shopJpaEntity)
                .on(brandPresetJpaEntity.shopId.eq(shopJpaEntity.id))
                .where(
                        brandMappingJpaEntity.internalBrandId.eq(internalBrandId),
                        shopJpaEntity.salesChannelId.eq(salesChannelId),
                        brandMappingJpaEntity.status.eq(STATUS_ACTIVE),
                        brandPresetJpaEntity.status.eq(STATUS_ACTIVE))
                .fetchFirst();
    }
}
