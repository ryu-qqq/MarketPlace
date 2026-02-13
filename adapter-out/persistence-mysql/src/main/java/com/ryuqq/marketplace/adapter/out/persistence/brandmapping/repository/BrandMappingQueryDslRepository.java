package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite.BrandMappingWithBrandDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.QBrandMappingJpaEntity;
import java.util.List;
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
}
