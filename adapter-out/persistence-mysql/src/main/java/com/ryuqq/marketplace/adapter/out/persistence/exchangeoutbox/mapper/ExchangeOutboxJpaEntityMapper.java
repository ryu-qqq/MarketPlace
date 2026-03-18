package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.id.ExchangeOutboxId;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxStatus;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import org.springframework.stereotype.Component;

/** ExchangeOutbox JPA Entity Mapper. */
@Component
public class ExchangeOutboxJpaEntityMapper {

    public ExchangeOutboxJpaEntity toEntity(ExchangeOutbox domain) {
        Long entityId = domain.isNew() ? null : domain.idValue();
        return ExchangeOutboxJpaEntity.create(
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

    public ExchangeOutbox toDomain(ExchangeOutboxJpaEntity entity) {
        return ExchangeOutbox.reconstitute(
                ExchangeOutboxId.of(entity.getId()),
                OrderItemId.of(entity.getOrderItemId()),
                ExchangeOutboxType.valueOf(entity.getOutboxType()),
                ExchangeOutboxStatus.valueOf(entity.getStatus()),
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
