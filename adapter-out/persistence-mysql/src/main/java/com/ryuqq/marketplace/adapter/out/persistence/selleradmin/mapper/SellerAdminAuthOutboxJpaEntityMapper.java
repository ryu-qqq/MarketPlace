package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminAuthOutboxId;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminAuthOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * SellerAdminAuthOutboxJpaEntityMapper - 셀러 관리자 인증 Outbox Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class SellerAdminAuthOutboxJpaEntityMapper {

    public SellerAdminAuthOutboxJpaEntity toEntity(SellerAdminAuthOutbox domain) {
        return SellerAdminAuthOutboxJpaEntity.create(
                domain.idValue(),
                domain.sellerAdminIdValue(),
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

    public SellerAdminAuthOutbox toDomain(SellerAdminAuthOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? SellerAdminAuthOutboxId.of(entity.getId())
                        : SellerAdminAuthOutboxId.forNew();
        SellerAdminId sellerAdminId =
                entity.getSellerAdminId() != null
                        ? SellerAdminId.of(entity.getSellerAdminId())
                        : null;
        return SellerAdminAuthOutbox.reconstitute(
                id,
                sellerAdminId,
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

    private SellerAdminAuthOutboxJpaEntity.Status toEntityStatus(
            SellerAdminAuthOutboxStatus status) {
        return switch (status) {
            case PENDING -> SellerAdminAuthOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> SellerAdminAuthOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> SellerAdminAuthOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> SellerAdminAuthOutboxJpaEntity.Status.FAILED;
        };
    }

    private SellerAdminAuthOutboxStatus toDomainStatus(
            SellerAdminAuthOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SellerAdminAuthOutboxStatus.PENDING;
            case PROCESSING -> SellerAdminAuthOutboxStatus.PROCESSING;
            case COMPLETED -> SellerAdminAuthOutboxStatus.COMPLETED;
            case FAILED -> SellerAdminAuthOutboxStatus.FAILED;
        };
    }
}
