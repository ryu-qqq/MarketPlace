package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity.ImageVariantSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import com.ryuqq.marketplace.domain.imagevariantsync.id.ImageVariantSyncOutboxId;
import org.springframework.stereotype.Component;

/**
 * ImageVariantSyncOutboxJpaEntityMapper - 이미지 Variant Sync Outbox Entity-Domain 매퍼.
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
public class ImageVariantSyncOutboxJpaEntityMapper {

    public ImageVariantSyncOutboxJpaEntity toEntity(ImageVariantSyncOutbox domain) {
        return ImageVariantSyncOutboxJpaEntity.create(
                domain.idValue(),
                domain.sourceImageId(),
                domain.sourceType(),
                domain.status(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version());
    }

    public ImageVariantSyncOutbox toDomain(ImageVariantSyncOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? ImageVariantSyncOutboxId.of(entity.getId())
                        : ImageVariantSyncOutboxId.forNew();
        return ImageVariantSyncOutbox.reconstitute(
                id,
                entity.getSourceImageId(),
                entity.getSourceType(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion());
    }
}
