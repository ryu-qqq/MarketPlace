package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping JPA Entity Mapper. */
@Component
public class InboundCategoryMappingJpaEntityMapper {

    public InboundCategoryMappingJpaEntity toEntity(InboundCategoryMapping mapping) {
        return InboundCategoryMappingJpaEntity.create(
                mapping.idValue(),
                mapping.inboundSourceId(),
                mapping.externalCategoryCode(),
                mapping.externalCategoryName(),
                mapping.internalCategoryId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }

    public InboundCategoryMapping toDomain(InboundCategoryMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = InboundCategoryMappingId.of(entity.getId());
        return InboundCategoryMapping.reconstitute(
                id,
                entity.getInboundSourceId(),
                entity.getExternalCategoryCode(),
                entity.getExternalCategoryName(),
                entity.getInternalCategoryId(),
                InboundCategoryMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
