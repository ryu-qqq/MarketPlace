package com.ryuqq.marketplace.adapter.out.persistence.brand.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandAliasJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.DataQuality;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Brand JPA Entity Mapper
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>Long FK 전략</li>
 *   <li>도메인 → Entity, Entity → 도메인 양방향 매핑</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Component
public class BrandJpaEntityMapper {

    /**
     * Domain Brand → JPA Entity 변환
     *
     * @param brand 도메인 Brand
     * @return BrandJpaEntity
     */
    public BrandJpaEntity toEntity(Brand brand) {
        return BrandJpaEntity.from(brand);
    }

    /**
     * JPA Entity → Domain Brand 변환 (Alias 포함)
     *
     * @param entity BrandJpaEntity
     * @param aliases BrandAliasJpaEntity 목록
     * @return Brand
     */
    public Brand toDomain(BrandJpaEntity entity, List<BrandAliasJpaEntity> aliases) {
        List<BrandAlias> aliasList = aliases.stream()
            .map(this::aliasToDomain)
            .toList();

        return Brand.reconstitute(
            BrandId.of(entity.getId()),
            BrandCode.of(entity.getCode()),
            CanonicalName.of(entity.getCanonicalName()),
            BrandName.of(entity.getNameKo(), entity.getNameEn(), entity.getShortName()),
            entity.getCountry() != null ? Country.of(entity.getCountry()) : null,
            entity.getDepartment(),
            entity.isLuxury(),
            entity.getStatus(),
            BrandMeta.of(entity.getOfficialWebsite(), entity.getLogoUrl(), entity.getDescription()),
            DataQuality.of(entity.getDataQualityLevel(), entity.getDataQualityScore()),
            aliasList,
            entity.getVersion()
        );
    }

    /**
     * JPA Entity → Domain Brand 변환 (Alias 없음)
     *
     * @param entity BrandJpaEntity
     * @return Brand
     */
    public Brand toDomainWithoutAliases(BrandJpaEntity entity) {
        return Brand.reconstitute(
            BrandId.of(entity.getId()),
            BrandCode.of(entity.getCode()),
            CanonicalName.of(entity.getCanonicalName()),
            BrandName.of(entity.getNameKo(), entity.getNameEn(), entity.getShortName()),
            entity.getCountry() != null ? Country.of(entity.getCountry()) : null,
            entity.getDepartment(),
            entity.isLuxury(),
            entity.getStatus(),
            BrandMeta.of(entity.getOfficialWebsite(), entity.getLogoUrl(), entity.getDescription()),
            DataQuality.of(entity.getDataQualityLevel(), entity.getDataQualityScore()),
            List.of(), // 빈 Alias 목록
            entity.getVersion()
        );
    }

    /**
     * Domain BrandAlias → JPA Entity 변환
     *
     * @param alias 도메인 BrandAlias
     * @return BrandAliasJpaEntity
     */
    public BrandAliasJpaEntity aliasToEntity(BrandAlias alias) {
        return BrandAliasJpaEntity.from(alias);
    }

    /**
     * JPA Entity → Domain BrandAlias 변환
     *
     * @param entity BrandAliasJpaEntity
     * @return BrandAlias
     */
    public BrandAlias aliasToDomain(BrandAliasJpaEntity entity) {
        return BrandAlias.reconstitute(
            BrandAliasId.of(entity.getId()),
            entity.getBrandId(),
            AliasName.of(entity.getAliasName(), entity.getNormalizedAlias()),
            AliasSource.of(
                entity.getSourceType(),
                entity.getSellerId(),
                entity.getMallCode()
            ),
            Confidence.of(entity.getConfidence()),
            entity.getStatus()
        );
    }
}
