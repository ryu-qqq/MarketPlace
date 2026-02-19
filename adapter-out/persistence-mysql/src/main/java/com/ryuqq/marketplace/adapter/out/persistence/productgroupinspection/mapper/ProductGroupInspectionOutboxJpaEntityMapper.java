package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.id.InspectionOutboxId;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * ProductGroupInspectionOutboxJpaEntityMapper - 상품 그룹 검수 Outbox Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class ProductGroupInspectionOutboxJpaEntityMapper {

    public ProductGroupInspectionOutboxJpaEntity toEntity(ProductGroupInspectionOutbox domain) {
        return ProductGroupInspectionOutboxJpaEntity.create(
                domain.idValue(),
                domain.productGroupId(),
                toEntityStatus(domain.status()),
                domain.inspectionResultJson(),
                domain.totalScore(),
                domain.passed(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version(),
                domain.idempotencyKeyValue());
    }

    public ProductGroupInspectionOutbox toDomain(ProductGroupInspectionOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? InspectionOutboxId.of(entity.getId())
                        : InspectionOutboxId.forNew();
        return ProductGroupInspectionOutbox.reconstitute(
                id,
                entity.getProductGroupId(),
                toDomainStatus(entity.getStatus()),
                entity.getInspectionResultJson(),
                entity.getTotalScore(),
                entity.getPassed(),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion(),
                entity.getIdempotencyKey());
    }

    private ProductGroupInspectionOutboxJpaEntity.Status toEntityStatus(
            InspectionOutboxStatus status) {
        return switch (status) {
            case PENDING -> ProductGroupInspectionOutboxJpaEntity.Status.PENDING;
            case SENT -> ProductGroupInspectionOutboxJpaEntity.Status.SENT;
            case SCORING -> ProductGroupInspectionOutboxJpaEntity.Status.SCORING;
            case ENHANCING -> ProductGroupInspectionOutboxJpaEntity.Status.ENHANCING;
            case VERIFYING -> ProductGroupInspectionOutboxJpaEntity.Status.VERIFYING;
            case COMPLETED -> ProductGroupInspectionOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> ProductGroupInspectionOutboxJpaEntity.Status.FAILED;
        };
    }

    private InspectionOutboxStatus toDomainStatus(
            ProductGroupInspectionOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> InspectionOutboxStatus.PENDING;
            case PROCESSING -> InspectionOutboxStatus.PENDING;
            case SENT -> InspectionOutboxStatus.SENT;
            case SCORING -> InspectionOutboxStatus.SCORING;
            case ENHANCING -> InspectionOutboxStatus.ENHANCING;
            case VERIFYING -> InspectionOutboxStatus.VERIFYING;
            case COMPLETED -> InspectionOutboxStatus.COMPLETED;
            case FAILED -> InspectionOutboxStatus.FAILED;
        };
    }
}
