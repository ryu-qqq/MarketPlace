package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.QBrandPresetJpaEntity.brandPresetJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity.shopJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite.BrandMappingWithBrandDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.QBrandMappingJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** BrandMapping QueryDSL Repository. */
@Repository
public class BrandMappingQueryDslRepository {

    private static final QBrandMappingJpaEntity brandMapping =
            QBrandMappingJpaEntity.brandMappingJpaEntity;
    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;

    private final JPAQueryFactory queryFactory;

    public BrandMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<BrandMappingWithBrandDto> findMappedBrandsByPresetId(Long presetId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                BrandMappingWithBrandDto.class,
                                brandMapping.id,
                                brandMapping.internalBrandId,
                                brand.nameKo))
                .from(brandMapping)
                .join(brand)
                .on(brandMapping.internalBrandId.eq(brand.id))
                .where(brandMapping.presetId.eq(presetId), brandMapping.status.eq("ACTIVE"))
                .fetch();
    }

    /**
     * 내부 브랜드 ID → 판매채널 브랜드 ID 역조회.
     *
     * <p>BrandMapping → BrandPreset → Shop 3-way JOIN.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID (매핑 없으면 empty)
     */
    public Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return Optional.ofNullable(
                queryFactory
                        .select(brandMapping.salesChannelBrandId)
                        .from(brandMapping)
                        .join(brandPresetJpaEntity)
                        .on(brandMapping.presetId.eq(brandPresetJpaEntity.id))
                        .join(shopJpaEntity)
                        .on(brandPresetJpaEntity.shopId.eq(shopJpaEntity.id))
                        .where(
                                brandMapping.internalBrandId.eq(internalBrandId),
                                shopJpaEntity.salesChannelId.eq(salesChannelId),
                                brandMapping.status.eq("ACTIVE"),
                                brandPresetJpaEntity.status.eq("ACTIVE"))
                        .fetchFirst());
    }

    /**
     * 내부 브랜드 ID → 외부 브랜드 코드 역조회.
     *
     * <p>BrandMapping → BrandPreset → Shop → SalesChannelBrand 4-way JOIN.
     */
    public Optional<String> findExternalBrandCode(Long salesChannelId, Long internalBrandId) {
        return Optional.ofNullable(
                queryFactory
                        .select(salesChannelBrandJpaEntity.externalBrandCode)
                        .from(brandMapping)
                        .join(brandPresetJpaEntity)
                        .on(brandMapping.presetId.eq(brandPresetJpaEntity.id))
                        .join(shopJpaEntity)
                        .on(brandPresetJpaEntity.shopId.eq(shopJpaEntity.id))
                        .join(salesChannelBrandJpaEntity)
                        .on(salesChannelBrandJpaEntity.id.eq(brandMapping.salesChannelBrandId))
                        .where(
                                brandMapping.internalBrandId.eq(internalBrandId),
                                shopJpaEntity.salesChannelId.eq(salesChannelId),
                                brandMapping.status.eq("ACTIVE"),
                                brandPresetJpaEntity.status.eq("ACTIVE"))
                        .fetchFirst());
    }
}
