package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import org.springframework.stereotype.Component;

/** InboundBrandMapping JPA Entity Mapper. */
@Component
public class InboundBrandMappingJpaEntityMapper {

    public InboundBrandMappingJpaEntity toEntity(InboundBrandMapping mapping) {
        return InboundBrandMappingJpaEntity.create(
                mapping.idValue(),
                mapping.inboundSourceId(),
                mapping.externalBrandCode(),
                mapping.externalBrandName(),
                mapping.internalBrandId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }

    public InboundBrandMapping toDomain(InboundBrandMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = InboundBrandMappingId.of(entity.getId());
        return InboundBrandMapping.reconstitute(
                id,
                entity.getInboundSourceId(),
                entity.getExternalBrandCode(),
                entity.getExternalBrandName(),
                entity.getInternalBrandId(),
                InboundBrandMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
