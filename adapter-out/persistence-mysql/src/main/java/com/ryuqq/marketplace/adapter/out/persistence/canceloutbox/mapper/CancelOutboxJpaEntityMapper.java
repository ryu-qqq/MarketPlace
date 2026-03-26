package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.id.CancelOutboxId;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxStatus;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import org.springframework.stereotype.Component;

/** CancelOutbox JPA Entity Mapper. */
@Component
public class CancelOutboxJpaEntityMapper {

    public CancelOutboxJpaEntity toEntity(CancelOutbox domain) {
        Long entityId = domain.isNew() ? null : domain.idValue();
        return CancelOutboxJpaEntity.create(
                entityId,
                domain.orderItemIdValue(),
                domain.outboxType().name(),
                domain.status().name(),
                domain.payload(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.processedAt(),
                domain.errorMessage(),
                domain.version(),
                domain.idempotencyKeyValue());
    }

    public CancelOutbox toDomain(CancelOutboxJpaEntity entity) {
        return CancelOutbox.reconstitute(
                CancelOutboxId.of(entity.getId()),
                OrderItemId.of(entity.getOrderItemId()),
                CancelOutboxType.valueOf(entity.getOutboxType()),
                CancelOutboxStatus.valueOf(entity.getStatus()),
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
}
