package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.DescriptionImageId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 상세설명 Entity ↔ 표준 도메인 매퍼. */
@Component
public class LegacyProductGroupDescriptionEntityMapper {

    public LegacyProductGroupDetailDescriptionEntity toEntity(ProductGroupDescription description) {
        return LegacyProductGroupDetailDescriptionEntity.createFull(
                description.productGroupIdValue(),
                description.contentValue(),
                description.cdnPathValue(),
                description.publishStatus() != null ? description.publishStatus().name() : null);
    }

    public LegacyDescriptionImageEntity toImageEntity(DescriptionImage image) {
        return LegacyDescriptionImageEntity.create(
                image.idValue(),
                image.productGroupDescriptionIdValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.sortOrder(),
                image.isDeleted(),
                image.isDeleted() ? image.deletionStatus().deletedAt() : null);
    }

    public ProductGroupDescription toDomain(
            LegacyProductGroupDetailDescriptionEntity desc,
            List<LegacyDescriptionImageEntity> imageEntities) {
        List<DescriptionImage> images = imageEntities.stream().map(this::toImageDomain).toList();

        return ProductGroupDescription.reconstitute(
                ProductGroupDescriptionId.of(desc.getProductGroupId()),
                ProductGroupId.of(desc.getProductGroupId()),
                DescriptionHtml.of(desc.getContent()),
                CdnPath.of(desc.getCdnPath()),
                desc.getPublishStatus() != null
                        ? DescriptionPublishStatus.valueOf(desc.getPublishStatus())
                        : DescriptionPublishStatus.PENDING,
                images,
                null,
                null);
    }

    public DescriptionImage toImageDomain(LegacyDescriptionImageEntity entity) {
        return DescriptionImage.reconstitute(
                DescriptionImageId.of(entity.getId()),
                ProductGroupDescriptionId.of(entity.getProductGroupId()),
                ImageUrl.of(entity.getOriginUrl()),
                ImageUrl.of(entity.getUploadedUrl()),
                entity.getSortOrder(),
                entity.isDeleted()
                        ? DeletionStatus.deletedAt(entity.getDeletedAt())
                        : DeletionStatus.active());
    }
}
