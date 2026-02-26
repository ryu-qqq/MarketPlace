package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
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

/**
 * ProductGroupDescriptionJpaEntityMapper - 상품 그룹 상세설명 JPA Entity Mapper.
 *
 * <p>PER-MAP-001: @Component 매퍼, 순수 변환, 도메인 reconstitute() 사용.
 */
@Component
public class ProductGroupDescriptionJpaEntityMapper {

    public ProductGroupDescriptionJpaEntity toEntity(ProductGroupDescription domain) {
        return ProductGroupDescriptionJpaEntity.create(
                domain.idValue(),
                domain.productGroupIdValue(),
                domain.contentValue(),
                domain.cdnPathValue(),
                domain.publishStatus().name(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public DescriptionImageJpaEntity toImageEntity(DescriptionImage image) {
        return DescriptionImageJpaEntity.create(
                image.idValue(),
                image.productGroupDescriptionIdValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.sortOrder(),
                image.isDeleted(),
                image.deletionStatus().deletedAt());
    }

    public ProductGroupDescription toDomain(
            ProductGroupDescriptionJpaEntity entity,
            List<DescriptionImageJpaEntity> imageEntities) {
        ProductGroupDescriptionId id =
                entity.getId() != null
                        ? ProductGroupDescriptionId.of(entity.getId())
                        : ProductGroupDescriptionId.forNew();

        List<DescriptionImage> images = imageEntities.stream().map(this::toImageDomain).toList();

        return ProductGroupDescription.reconstitute(
                id,
                ProductGroupId.of(entity.getProductGroupId()),
                DescriptionHtml.of(entity.getContent()),
                entity.getCdnPath() != null ? CdnPath.of(entity.getCdnPath()) : null,
                DescriptionPublishStatus.valueOf(entity.getPublishStatus()),
                images,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public DescriptionImage toImageDomain(DescriptionImageJpaEntity entity) {
        DescriptionImageId id =
                entity.getId() != null
                        ? DescriptionImageId.of(entity.getId())
                        : DescriptionImageId.forNew();

        return DescriptionImage.reconstitute(
                id,
                ProductGroupDescriptionId.of(entity.getProductGroupDescriptionId()),
                ImageUrl.of(entity.getOriginUrl()),
                entity.getUploadedUrl() != null ? ImageUrl.of(entity.getUploadedUrl()) : null,
                entity.getSortOrder(),
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()));
    }
}
