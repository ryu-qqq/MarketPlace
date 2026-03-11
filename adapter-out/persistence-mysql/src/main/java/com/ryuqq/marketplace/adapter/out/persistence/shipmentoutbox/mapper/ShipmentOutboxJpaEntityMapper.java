package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.id.ShipmentOutboxId;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxStatus;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import org.springframework.stereotype.Component;

/** ShipmentOutbox 도메인 ↔ JPA 엔티티 매퍼. */
@Component
public class ShipmentOutboxJpaEntityMapper {

    public ShipmentOutboxJpaEntity toEntity(ShipmentOutbox outbox) {
        return ShipmentOutboxJpaEntity.of(
                outbox.isNew() ? null : outbox.idValue(),
                outbox.orderItemIdValue(),
                toEntityOutboxType(outbox.outboxType()),
                toEntityStatus(outbox.status()),
                outbox.payload(),
                outbox.retryCount(),
                outbox.maxRetry(),
                outbox.createdAt(),
                outbox.updatedAt(),
                outbox.processedAt(),
                outbox.errorMessage(),
                outbox.version(),
                outbox.idempotencyKeyValue());
    }

    public ShipmentOutbox toDomain(ShipmentOutboxJpaEntity entity) {
        return ShipmentOutbox.reconstitute(
                ShipmentOutboxId.of(entity.getId()),
                OrderItemId.of(entity.getOrderItemId()),
                toDomainOutboxType(entity.getOutboxType()),
                toDomainStatus(entity.getStatus()),
                entity.getPayload(),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion(),
                entity.getIdempotencyKey());
    }

    private ShipmentOutboxJpaEntity.OutboxType toEntityOutboxType(ShipmentOutboxType type) {
        return ShipmentOutboxJpaEntity.OutboxType.valueOf(type.name());
    }

    private ShipmentOutboxType toDomainOutboxType(ShipmentOutboxJpaEntity.OutboxType type) {
        return ShipmentOutboxType.valueOf(type.name());
    }

    private ShipmentOutboxJpaEntity.Status toEntityStatus(ShipmentOutboxStatus status) {
        return ShipmentOutboxJpaEntity.Status.valueOf(status.name());
    }

    private ShipmentOutboxStatus toDomainStatus(ShipmentOutboxJpaEntity.Status status) {
        return ShipmentOutboxStatus.valueOf(status.name());
    }
}
