package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.id.RefundOutboxId;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxStatus;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import org.springframework.stereotype.Component;

/** RefundOutbox JPA Entity Mapper. */
@Component
public class RefundOutboxJpaEntityMapper {

    public RefundOutboxJpaEntity toEntity(RefundOutbox domain) {
        Long entityId = domain.isNew() ? null : domain.idValue();
        return RefundOutboxJpaEntity.create(
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

    public RefundOutbox toDomain(RefundOutboxJpaEntity entity) {
        return RefundOutbox.reconstitute(
                RefundOutboxId.of(entity.getId()),
                OrderItemId.of(entity.getOrderItemId()),
                RefundOutboxType.valueOf(entity.getOutboxType()),
                RefundOutboxStatus.valueOf(entity.getStatus()),
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
