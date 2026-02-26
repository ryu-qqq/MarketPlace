package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyProductIdMappingId;
import org.springframework.stereotype.Component;

/**
 * LegacyProductIdMappingJpaEntityMapper - Entity ↔ Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class LegacyProductIdMappingJpaEntityMapper {

    public LegacyProductIdMappingJpaEntity toEntity(LegacyProductIdMapping domain) {
        return LegacyProductIdMappingJpaEntity.create(
                domain.idValue(),
                domain.legacyProductId(),
                domain.internalProductId(),
                domain.legacyProductGroupId(),
                domain.internalProductGroupId(),
                domain.createdAt());
    }

    public LegacyProductIdMapping toDomain(LegacyProductIdMappingJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? LegacyProductIdMappingId.of(entity.getId())
                        : LegacyProductIdMappingId.forNew();
        return LegacyProductIdMapping.reconstitute(
                id,
                entity.getLegacyProductId(),
                entity.getInternalProductId(),
                entity.getLegacyProductGroupId(),
                entity.getInternalProductGroupId(),
                entity.getCreatedAt());
    }
}
