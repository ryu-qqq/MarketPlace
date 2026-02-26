package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerAuthOutboxId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * SellerAuthOutboxJpaEntityMapper - 셀러 인증 Outbox Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class SellerAuthOutboxJpaEntityMapper {

    public SellerAuthOutboxJpaEntity toEntity(SellerAuthOutbox domain) {
        return SellerAuthOutboxJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.payload(),
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

    public SellerAuthOutbox toDomain(SellerAuthOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? SellerAuthOutboxId.of(entity.getId())
                        : SellerAuthOutboxId.forNew();
        return SellerAuthOutbox.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                entity.getPayload(),
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

    private SellerAuthOutboxJpaEntity.Status toEntityStatus(SellerAuthOutboxStatus status) {
        return switch (status) {
            case PENDING -> SellerAuthOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> SellerAuthOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> SellerAuthOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> SellerAuthOutboxJpaEntity.Status.FAILED;
        };
    }

    private SellerAuthOutboxStatus toDomainStatus(SellerAuthOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SellerAuthOutboxStatus.PENDING;
            case PROCESSING -> SellerAuthOutboxStatus.PROCESSING;
            case COMPLETED -> SellerAuthOutboxStatus.COMPLETED;
            case FAILED -> SellerAuthOutboxStatus.FAILED;
        };
    }
}
