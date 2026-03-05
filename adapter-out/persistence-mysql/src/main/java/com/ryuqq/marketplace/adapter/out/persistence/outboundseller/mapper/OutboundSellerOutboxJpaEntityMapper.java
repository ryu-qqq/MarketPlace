package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.OutboundSellerOutboxJpaEntity;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import com.ryuqq.marketplace.domain.outboundseller.id.OutboundSellerOutboxId;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerEntityType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOutboxStatus;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

@Component
public class OutboundSellerOutboxJpaEntityMapper {

    public OutboundSellerOutboxJpaEntity toEntity(OutboundSellerOutbox domain) {
        return OutboundSellerOutboxJpaEntity.create(
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

    public OutboundSellerOutbox toDomain(OutboundSellerOutboxJpaEntity entity) {
        return OutboundSellerOutbox.reconstitute(
                OutboundSellerOutboxId.of(entity.getId()),
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

    private OutboundSellerOutboxJpaEntity.Status toEntityStatus(OutboundSellerOutboxStatus status) {
        return switch (status) {
            case PENDING -> OutboundSellerOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> OutboundSellerOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> OutboundSellerOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> OutboundSellerOutboxJpaEntity.Status.FAILED;
        };
    }

    private OutboundSellerOutboxStatus toDomainStatus(OutboundSellerOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> OutboundSellerOutboxStatus.PENDING;
            case PROCESSING -> OutboundSellerOutboxStatus.PROCESSING;
            case COMPLETED -> OutboundSellerOutboxStatus.COMPLETED;
            case FAILED -> OutboundSellerOutboxStatus.FAILED;
        };
    }

    private OutboundSellerOutboxJpaEntity.EntityType toEntityType(OutboundSellerEntityType type) {
        return switch (type) {
            case SELLER -> OutboundSellerOutboxJpaEntity.EntityType.SELLER;
            case SHIPPING_POLICY -> OutboundSellerOutboxJpaEntity.EntityType.SHIPPING_POLICY;
            case REFUND_POLICY -> OutboundSellerOutboxJpaEntity.EntityType.REFUND_POLICY;
            case SELLER_ADDRESS -> OutboundSellerOutboxJpaEntity.EntityType.SELLER_ADDRESS;
        };
    }

    private OutboundSellerEntityType toDomainEntityType(
            OutboundSellerOutboxJpaEntity.EntityType type) {
        return switch (type) {
            case SELLER -> OutboundSellerEntityType.SELLER;
            case SHIPPING_POLICY -> OutboundSellerEntityType.SHIPPING_POLICY;
            case REFUND_POLICY -> OutboundSellerEntityType.REFUND_POLICY;
            case SELLER_ADDRESS -> OutboundSellerEntityType.SELLER_ADDRESS;
        };
    }

    private OutboundSellerOutboxJpaEntity.OperationType toOperationType(
            OutboundSellerOperationType type) {
        return switch (type) {
            case CREATE -> OutboundSellerOutboxJpaEntity.OperationType.CREATE;
            case UPDATE -> OutboundSellerOutboxJpaEntity.OperationType.UPDATE;
            case DELETE -> OutboundSellerOutboxJpaEntity.OperationType.DELETE;
        };
    }

    private OutboundSellerOperationType toDomainOperationType(
            OutboundSellerOutboxJpaEntity.OperationType type) {
        return switch (type) {
            case CREATE -> OutboundSellerOperationType.CREATE;
            case UPDATE -> OutboundSellerOperationType.UPDATE;
            case DELETE -> OutboundSellerOperationType.DELETE;
        };
    }
}
