package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import org.springframework.stereotype.Component;

/** 레거시 상품그룹 이미지 Entity ↔ 표준 도메인 매퍼. */
@Component
public class LegacyProductGroupImageEntityMapper {

    public LegacyProductGroupImageEntity toEntity(ProductGroupImage image) {
        return LegacyProductGroupImageEntity.create(
                image.idValue(),
                image.productGroupIdValue(),
                image.imageTypeName(),
                image.originUrlValue(),
                image.originUrlValue(),
                image.sortOrder(),
                image.isDeleted() ? "Y" : "N");
    }

    public ProductGroupImage toDomain(LegacyProductGroupImageEntity entity) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                ImageUrl.of(entity.getOriginUrl()),
                ImageUrl.of(entity.getImageUrl()),
                ImageType.valueOf(entity.getProductGroupImageType()),
                entity.getDisplayOrder().intValue(),
                "Y".equals(entity.getDeleteYn())
                        ? DeletionStatus.deletedAt(Instant.now())
                        : DeletionStatus.active());
    }
}
