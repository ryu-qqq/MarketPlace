package com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.id.ImageUploadOutboxId;
import org.springframework.stereotype.Component;

/**
 * ImageUploadOutboxJpaEntityMapper - 이미지 업로드 Outbox Entity-Domain 매퍼.
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
public class ImageUploadOutboxJpaEntityMapper {

    public ImageUploadOutboxJpaEntity toEntity(ImageUploadOutbox domain) {
        return ImageUploadOutboxJpaEntity.create(
                domain.idValue(),
                domain.sourceId(),
                domain.sourceType(),
                domain.originUrlValue(),
                domain.status(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version(),
                domain.idempotencyKeyValue(),
                domain.downloadTaskId());
    }

    public ImageUploadOutbox toDomain(ImageUploadOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? ImageUploadOutboxId.of(entity.getId())
                        : ImageUploadOutboxId.forNew();
        return ImageUploadOutbox.reconstitute(
                id,
                entity.getSourceId(),
                entity.getSourceType(),
                entity.getOriginUrl(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion(),
                entity.getIdempotencyKey(),
                entity.getDownloadTaskId());
    }
}
