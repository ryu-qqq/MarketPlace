package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * LegacyConversionOutboxJpaEntityMapper - Entity ↔ Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class LegacyConversionOutboxJpaEntityMapper {

    public LegacyConversionOutboxJpaEntity toEntity(LegacyConversionOutbox domain) {
        return LegacyConversionOutboxJpaEntity.create(
                domain.idValue(),
                domain.legacyProductGroupId(),
                toEntityStatus(domain.status()),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version());
    }

    public LegacyConversionOutbox toDomain(LegacyConversionOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? LegacyConversionOutboxId.of(entity.getId())
                        : LegacyConversionOutboxId.forNew();
        return LegacyConversionOutbox.reconstitute(
                id,
                entity.getLegacyProductGroupId(),
                toDomainStatus(entity.getStatus()),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion());
    }

    private LegacyConversionOutboxJpaEntity.Status toEntityStatus(
            LegacyConversionOutboxStatus status) {
        return switch (status) {
            case PENDING -> LegacyConversionOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> LegacyConversionOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> LegacyConversionOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> LegacyConversionOutboxJpaEntity.Status.FAILED;
        };
    }

    private LegacyConversionOutboxStatus toDomainStatus(
            LegacyConversionOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> LegacyConversionOutboxStatus.PENDING;
            case PROCESSING -> LegacyConversionOutboxStatus.PROCESSING;
            case COMPLETED -> LegacyConversionOutboxStatus.COMPLETED;
            case FAILED -> LegacyConversionOutboxStatus.FAILED;
        };
    }
}
