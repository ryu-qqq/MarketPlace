package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import com.ryuqq.marketplace.domain.productintelligence.id.IntelligenceOutboxId;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * IntelligenceOutboxJpaEntityMapper - Intelligence Pipeline Outbox Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class IntelligenceOutboxJpaEntityMapper {

    public IntelligenceOutboxJpaEntity toEntity(IntelligenceOutbox domain) {
        return IntelligenceOutboxJpaEntity.create(
                domain.idValue(),
                domain.productGroupId(),
                domain.profileId(),
                toEntityStatus(domain.status()),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version(),
                domain.idempotencyKeyValue());
    }

    public IntelligenceOutbox toDomain(IntelligenceOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? IntelligenceOutboxId.of(entity.getId())
                        : IntelligenceOutboxId.forNew();
        return IntelligenceOutbox.reconstitute(
                id,
                entity.getProductGroupId(),
                entity.getProfileId(),
                toDomainStatus(entity.getStatus()),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion(),
                entity.getIdempotencyKey());
    }

    private IntelligenceOutboxJpaEntity.Status toEntityStatus(IntelligenceOutboxStatus status) {
        return switch (status) {
            case PENDING -> IntelligenceOutboxJpaEntity.Status.PENDING;
            case SENT -> IntelligenceOutboxJpaEntity.Status.SENT;
            case COMPLETED -> IntelligenceOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> IntelligenceOutboxJpaEntity.Status.FAILED;
        };
    }

    private IntelligenceOutboxStatus toDomainStatus(IntelligenceOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> IntelligenceOutboxStatus.PENDING;
            case SENT -> IntelligenceOutboxStatus.SENT;
            case COMPLETED -> IntelligenceOutboxStatus.COMPLETED;
            case FAILED -> IntelligenceOutboxStatus.FAILED;
        };
    }
}
