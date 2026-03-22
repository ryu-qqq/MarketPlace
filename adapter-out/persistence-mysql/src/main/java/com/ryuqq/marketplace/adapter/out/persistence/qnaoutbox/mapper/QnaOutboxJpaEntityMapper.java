package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import org.springframework.stereotype.Component;

/** QnaOutbox JPA Entity Mapper. */
@Component
public class QnaOutboxJpaEntityMapper {

    public QnaOutboxJpaEntity toEntity(QnaOutbox outbox) {
        return QnaOutboxJpaEntity.create(
                outbox.isNew() ? null : outbox.idValue(),
                outbox.qnaIdValue(),
                outbox.salesChannelId(),
                outbox.externalQnaId(),
                outbox.outboxType().name(),
                QnaOutboxJpaEntity.Status.valueOf(outbox.status().name()),
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

    public QnaOutbox toDomain(QnaOutboxJpaEntity entity) {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(entity.getId()),
                QnaId.of(entity.getQnaId()),
                entity.getSalesChannelId(),
                entity.getExternalQnaId(),
                QnaOutboxType.valueOf(entity.getOutboxType()),
                QnaOutboxStatus.valueOf(entity.getStatus().name()),
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
