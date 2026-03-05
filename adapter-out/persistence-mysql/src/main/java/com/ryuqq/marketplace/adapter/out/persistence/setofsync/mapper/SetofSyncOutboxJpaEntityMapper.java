package com.ryuqq.marketplace.adapter.out.persistence.setofsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.SetofSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.id.SetofSyncOutboxId;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOutboxStatus;
import org.springframework.stereotype.Component;

@Component
public class SetofSyncOutboxJpaEntityMapper {

    public SetofSyncOutboxJpaEntity toEntity(SetofSyncOutbox domain) {
        return SetofSyncOutboxJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.entityId(),
                toEntityType(domain.entityType()),
                toOperationType(domain.operationType()),
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

    public SetofSyncOutbox toDomain(SetofSyncOutboxJpaEntity entity) {
        return SetofSyncOutbox.reconstitute(
                SetofSyncOutboxId.of(entity.getId()),
                SellerId.of(entity.getSellerId()),
                entity.getEntityId(),
                toDomainEntityType(entity.getEntityType()),
                toDomainOperationType(entity.getOperationType()),
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

    private SetofSyncOutboxJpaEntity.Status toEntityStatus(SetofSyncOutboxStatus status) {
        return switch (status) {
            case PENDING -> SetofSyncOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> SetofSyncOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> SetofSyncOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> SetofSyncOutboxJpaEntity.Status.FAILED;
        };
    }

    private SetofSyncOutboxStatus toDomainStatus(SetofSyncOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SetofSyncOutboxStatus.PENDING;
            case PROCESSING -> SetofSyncOutboxStatus.PROCESSING;
            case COMPLETED -> SetofSyncOutboxStatus.COMPLETED;
            case FAILED -> SetofSyncOutboxStatus.FAILED;
        };
    }

    private SetofSyncOutboxJpaEntity.EntityType toEntityType(SetofSyncEntityType type) {
        return switch (type) {
            case SELLER -> SetofSyncOutboxJpaEntity.EntityType.SELLER;
            case SHIPPING_POLICY -> SetofSyncOutboxJpaEntity.EntityType.SHIPPING_POLICY;
            case REFUND_POLICY -> SetofSyncOutboxJpaEntity.EntityType.REFUND_POLICY;
            case SELLER_ADDRESS -> SetofSyncOutboxJpaEntity.EntityType.SELLER_ADDRESS;
        };
    }

    private SetofSyncEntityType toDomainEntityType(SetofSyncOutboxJpaEntity.EntityType type) {
        return switch (type) {
            case SELLER -> SetofSyncEntityType.SELLER;
            case SHIPPING_POLICY -> SetofSyncEntityType.SHIPPING_POLICY;
            case REFUND_POLICY -> SetofSyncEntityType.REFUND_POLICY;
            case SELLER_ADDRESS -> SetofSyncEntityType.SELLER_ADDRESS;
        };
    }

    private SetofSyncOutboxJpaEntity.OperationType toOperationType(SetofSyncOperationType type) {
        return switch (type) {
            case CREATE -> SetofSyncOutboxJpaEntity.OperationType.CREATE;
            case UPDATE -> SetofSyncOutboxJpaEntity.OperationType.UPDATE;
            case DELETE -> SetofSyncOutboxJpaEntity.OperationType.DELETE;
        };
    }

    private SetofSyncOperationType toDomainOperationType(
            SetofSyncOutboxJpaEntity.OperationType type) {
        return switch (type) {
            case CREATE -> SetofSyncOperationType.CREATE;
            case UPDATE -> SetofSyncOperationType.UPDATE;
            case DELETE -> SetofSyncOperationType.DELETE;
        };
    }
}
