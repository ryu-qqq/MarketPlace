package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.id.ImageVariantId;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import org.springframework.stereotype.Component;

/**
 * ImageVariantJpaEntityMapper - 이미지 Variant Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class ImageVariantJpaEntityMapper {

    public ImageVariantJpaEntity toEntity(ImageVariant domain) {
        return ImageVariantJpaEntity.create(
                domain.idValue(),
                domain.sourceImageId(),
                domain.sourceType(),
                domain.variantType(),
                domain.resultAssetIdValue(),
                domain.variantUrlValue(),
                domain.width(),
                domain.height(),
                domain.createdAt());
    }

    public ImageVariant toDomain(ImageVariantJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? ImageVariantId.of(entity.getId())
                        : ImageVariantId.forNew();
        return ImageVariant.reconstitute(
                id,
                entity.getSourceImageId(),
                entity.getSourceType(),
                entity.getVariantType(),
                ResultAssetId.of(entity.getResultAssetId()),
                ImageUrl.of(entity.getVariantUrl()),
                ImageDimension.of(entity.getWidth(), entity.getHeight()),
                entity.getCreatedAt());
    }
}
