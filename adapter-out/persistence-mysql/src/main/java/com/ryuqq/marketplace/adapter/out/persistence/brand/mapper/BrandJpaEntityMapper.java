package com.ryuqq.marketplace.adapter.out.persistence.brand.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.LogoUrl;
import org.springframework.stereotype.Component;

/** Brand JPA Entity Mapper. */
@Component
public class BrandJpaEntityMapper {

    public BrandJpaEntity toEntity(Brand brand) {
        return BrandJpaEntity.create(
                brand.idValue(),
                brand.codeValue(),
                brand.nameKo(),
                brand.nameEn(),
                brand.shortName(),
                brand.status().name(),
                brand.logoUrlValue(),
                brand.createdAt(),
                brand.updatedAt(),
                brand.deletedAt());
    }

    public Brand toDomain(BrandJpaEntity entity) {
        var id = entity.getId() != null ? BrandId.of(entity.getId()) : BrandId.forNew();
        return Brand.reconstitute(
                id,
                BrandCode.of(entity.getCode()),
                BrandName.of(entity.getNameKo(), entity.getNameEn(), entity.getShortName()),
                BrandStatus.fromString(entity.getStatus()),
                LogoUrl.of(entity.getLogoUrl()),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
