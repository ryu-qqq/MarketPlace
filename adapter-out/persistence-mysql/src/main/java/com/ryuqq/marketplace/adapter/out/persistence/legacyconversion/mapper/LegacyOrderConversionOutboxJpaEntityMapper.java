package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderConversionOutboxJpaEntityMapper - Entity ↔ Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class LegacyOrderConversionOutboxJpaEntityMapper {

    public LegacyOrderConversionOutboxJpaEntity toEntity(LegacyOrderConversionOutbox domain) {
        return LegacyOrderConversionOutboxJpaEntity.create(
                domain.idValue(),
                domain.legacyOrderId(),
                domain.legacyPaymentId(),
                domain.status().name(),
                domain.retryCount(),
                domain.maxRetry(),
                domain.errorMessage(),
                domain.processedAt(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.version());
    }

    public LegacyOrderConversionOutbox toDomain(LegacyOrderConversionOutboxJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? LegacyOrderConversionOutboxId.of(entity.getId())
                        : LegacyOrderConversionOutboxId.forNew();
        return LegacyOrderConversionOutbox.reconstitute(
                id,
                entity.getLegacyOrderId(),
                entity.getLegacyPaymentId(),
                LegacyConversionOutboxStatus.valueOf(entity.getStatus()),
                entity.getRetryCount(),
                entity.getMaxRetry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt(),
                entity.getErrorMessage(),
                entity.getVersion());
    }
}
