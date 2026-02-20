package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.ExternalProductSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import com.ryuqq.marketplace.domain.externalproductsync.id.ExternalProductSyncOutboxId;
import com.ryuqq.marketplace.domain.externalproductsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.externalproductsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/** ExternalProductSyncOutbox Domain ↔ JPA Entity 변환 매퍼. */
@Component
public class ExternalProductSyncOutboxJpaEntityMapper {

    public ExternalProductSyncOutboxJpaEntity toEntity(ExternalProductSyncOutbox domain) {
        return ExternalProductSyncOutboxJpaEntity.create(
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

    public ExternalProductSyncOutbox toDomain(ExternalProductSyncOutboxJpaEntity entity) {
        ExternalProductSyncOutboxId id =
                entity.getId() != null
                        ? ExternalProductSyncOutboxId.of(entity.getId())
                        : ExternalProductSyncOutboxId.forNew();
        return ExternalProductSyncOutbox.reconstitute(
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

    private ExternalProductSyncOutboxJpaEntity.SyncType toEntitySyncType(SyncType syncType) {
        return switch (syncType) {
            case CREATE -> ExternalProductSyncOutboxJpaEntity.SyncType.CREATE;
            case UPDATE -> ExternalProductSyncOutboxJpaEntity.SyncType.UPDATE;
            case DELETE -> ExternalProductSyncOutboxJpaEntity.SyncType.DELETE;
        };
    }

    private SyncType toDomainSyncType(ExternalProductSyncOutboxJpaEntity.SyncType syncType) {
        return switch (syncType) {
            case CREATE -> SyncType.CREATE;
            case UPDATE -> SyncType.UPDATE;
            case DELETE -> SyncType.DELETE;
        };
    }

    private ExternalProductSyncOutboxJpaEntity.Status toEntityStatus(SyncStatus status) {
        return switch (status) {
            case PENDING -> ExternalProductSyncOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> ExternalProductSyncOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> ExternalProductSyncOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> ExternalProductSyncOutboxJpaEntity.Status.FAILED;
        };
    }

    private SyncStatus toDomainStatus(ExternalProductSyncOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SyncStatus.PENDING;
            case PROCESSING -> SyncStatus.PROCESSING;
            case COMPLETED -> SyncStatus.COMPLETED;
            case FAILED -> SyncStatus.FAILED;
        };
    }
}
