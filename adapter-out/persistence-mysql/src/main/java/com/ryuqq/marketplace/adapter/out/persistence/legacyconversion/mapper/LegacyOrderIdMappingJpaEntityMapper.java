package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderIdMappingJpaEntityMapper - Entity ↔ Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class LegacyOrderIdMappingJpaEntityMapper {

    public LegacyOrderIdMappingJpaEntity toEntity(LegacyOrderIdMapping domain) {
        return LegacyOrderIdMappingJpaEntity.create(
                domain.idValue(),
                domain.legacyOrderId(),
                domain.legacyPaymentId(),
                domain.internalOrderId(),
                domain.salesChannelId(),
                domain.channelName(),
                domain.createdAt());
    }

    public LegacyOrderIdMapping toDomain(LegacyOrderIdMappingJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? LegacyOrderIdMappingId.of(entity.getId())
                        : LegacyOrderIdMappingId.forNew();
        return LegacyOrderIdMapping.reconstitute(
                id,
                entity.getLegacyOrderId(),
                entity.getLegacyPaymentId(),
                entity.getInternalOrderId(),
                entity.getSalesChannelId(),
                entity.getChannelName(),
                entity.getCreatedAt());
    }
}
