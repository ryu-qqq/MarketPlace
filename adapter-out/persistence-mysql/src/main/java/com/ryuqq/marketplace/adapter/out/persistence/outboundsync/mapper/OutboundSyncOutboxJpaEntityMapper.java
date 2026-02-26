package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.id.OutboundSyncOutboxId;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/** OutboundSyncOutbox Domain ↔ JPA Entity 변환 매퍼. */
@Component
public class OutboundSyncOutboxJpaEntityMapper {

    public OutboundSyncOutboxJpaEntity toEntity(OutboundSyncOutbox domain) {
        return OutboundSyncOutboxJpaEntity.of(
                domain.idValue(),
                domain.productGroupIdValue(),
                domain.salesChannelIdValue(),
                domain.sellerIdValue(),
                toEntitySyncType(domain.syncType()),
                toEntityStatus(domain.status()),
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

    public OutboundSyncOutbox toDomain(OutboundSyncOutboxJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException(
                    "OutboundSyncOutboxJpaEntity.id가 null입니다. DB에서 복원된 엔티티에 ID가 없을 수 없습니다.");
        }
        OutboundSyncOutboxId id = OutboundSyncOutboxId.of(entity.getId());
        return OutboundSyncOutbox.reconstitute(
                id,
                ProductGroupId.of(entity.getProductGroupId()),
                SalesChannelId.of(entity.getSalesChannelId()),
                SellerId.of(entity.getSellerId()),
                toDomainSyncType(entity.getSyncType()),
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

    private OutboundSyncOutboxJpaEntity.SyncType toEntitySyncType(SyncType syncType) {
        return switch (syncType) {
            case CREATE -> OutboundSyncOutboxJpaEntity.SyncType.CREATE;
            case UPDATE -> OutboundSyncOutboxJpaEntity.SyncType.UPDATE;
            case DELETE -> OutboundSyncOutboxJpaEntity.SyncType.DELETE;
        };
    }

    private SyncType toDomainSyncType(OutboundSyncOutboxJpaEntity.SyncType syncType) {
        return switch (syncType) {
            case CREATE -> SyncType.CREATE;
            case UPDATE -> SyncType.UPDATE;
            case DELETE -> SyncType.DELETE;
        };
    }

    private OutboundSyncOutboxJpaEntity.Status toEntityStatus(SyncStatus status) {
        return switch (status) {
            case PENDING -> OutboundSyncOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> OutboundSyncOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> OutboundSyncOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> OutboundSyncOutboxJpaEntity.Status.FAILED;
        };
    }

    private SyncStatus toDomainStatus(OutboundSyncOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SyncStatus.PENDING;
            case PROCESSING -> SyncStatus.PROCESSING;
            case COMPLETED -> SyncStatus.COMPLETED;
            case FAILED -> SyncStatus.FAILED;
        };
    }
}
