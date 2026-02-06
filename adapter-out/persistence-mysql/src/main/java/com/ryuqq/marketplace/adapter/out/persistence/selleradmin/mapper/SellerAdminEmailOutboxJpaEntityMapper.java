package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminEmailOutboxId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * SellerAdminEmailOutboxJpaEntityMapper - 셀러 관리자 이메일 Outbox Entity-Domain 매퍼.
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
public class SellerAdminEmailOutboxJpaEntityMapper {

    public SellerAdminEmailOutboxJpaEntity toEntity(SellerAdminEmailOutbox domain) {
        return SellerAdminEmailOutboxJpaEntity.create(
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

    public SellerAdminEmailOutbox toDomain(SellerAdminEmailOutboxJpaEntity entity) {
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(entity.getId()),
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

    private SellerAdminEmailOutboxJpaEntity.Status toEntityStatus(
            SellerAdminEmailOutboxStatus status) {
        return switch (status) {
            case PENDING -> SellerAdminEmailOutboxJpaEntity.Status.PENDING;
            case PROCESSING -> SellerAdminEmailOutboxJpaEntity.Status.PROCESSING;
            case COMPLETED -> SellerAdminEmailOutboxJpaEntity.Status.COMPLETED;
            case FAILED -> SellerAdminEmailOutboxJpaEntity.Status.FAILED;
        };
    }

    private SellerAdminEmailOutboxStatus toDomainStatus(
            SellerAdminEmailOutboxJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> SellerAdminEmailOutboxStatus.PENDING;
            case PROCESSING -> SellerAdminEmailOutboxStatus.PROCESSING;
            case COMPLETED -> SellerAdminEmailOutboxStatus.COMPLETED;
            case FAILED -> SellerAdminEmailOutboxStatus.FAILED;
        };
    }
}
