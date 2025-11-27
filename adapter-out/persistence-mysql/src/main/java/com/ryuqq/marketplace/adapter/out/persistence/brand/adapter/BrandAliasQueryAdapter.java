package com.ryuqq.marketplace.adapter.out.persistence.brand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandAliasJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandAliasJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandQueryDslRepository;
import com.ryuqq.marketplace.application.brand.port.out.query.AliasMatchResult;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandAliasProjection;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandAliasQueryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BrandAlias Query Adapter
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>Long FK 전략</li>
 *   <li>생성자 주입</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Component
public class BrandAliasQueryAdapter implements BrandAliasQueryPort {

    private final BrandAliasJpaRepository aliasRepository;
    private final BrandQueryDslRepository queryDslRepository;

    public BrandAliasQueryAdapter(
        BrandAliasJpaRepository aliasRepository,
        BrandQueryDslRepository queryDslRepository
    ) {
        this.aliasRepository = aliasRepository;
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias) {
        return queryDslRepository.findByNormalizedAlias(normalizedAlias);
    }

    @Override
    public List<BrandAliasProjection> searchByKeyword(String keyword) {
        // TODO: QueryDSL로 구현 (키워드 검색)
        return List.of();
    }

    @Override
    public List<BrandAliasProjection> findByBrandId(Long brandId) {
        return aliasRepository.findByBrandId(brandId).stream()
            .map(this::toProjection)
            .toList();
    }

    /**
     * Entity → Projection 변환
     *
     * @param entity BrandAliasJpaEntity
     * @return BrandAliasProjection
     */
    private BrandAliasProjection toProjection(BrandAliasJpaEntity entity) {
        return new BrandAliasProjection(
            entity.getId(),
            entity.getBrandId(),
            entity.getAliasName(),
            entity.getNormalizedAlias(),
            entity.getSourceType().name(),
            entity.getSellerId(),
            entity.getMallCode(),
            entity.getConfidence(),
            entity.getStatus().name()
        );
    }
}
