package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagetransform.id.ImageTransformOutboxId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import org.springframework.stereotype.Component;

/**
 * ImageTransformOutboxJpaEntityMapper - 이미지 변환 Outbox Entity-Domain 매퍼.
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
public class ImageTransformOutboxJpaEntityMapper {

    public ImageTransformOutboxJpaEntity toEntity(ImageTransformOutbox domain) {
        return ImageTransformOutboxJpaEntity.create(
                domain.idValue(),
                domain.sourceImageId(),
                domain.sourceType(),
                domain.uploadedUrlValue(),
                domain.variantType(),
                domain.fileAssetId(),
                domain.transformRequestId(),
                domain.status(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version(),
                domain.idempotencyKeyValue());
    }

    public ImageTransformOutbox toDomain(ImageTransformOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? ImageTransformOutboxId.of(entity.getId())
                        : ImageTransformOutboxId.forNew();
        return ImageTransformOutbox.reconstitute(
                id,
                entity.getSourceImageId(),
                entity.getSourceType(),
                ImageUrl.of(entity.getUploadedUrl()),
                entity.getVariantType(),
                entity.getFileAssetId(),
                entity.getTransformRequestId(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion(),
                entity.getIdempotencyKey());
    }
}
