package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.ExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping JPA Entity Mapper. */
@Component
public class ExternalBrandMappingJpaEntityMapper {

    public ExternalBrandMappingJpaEntity toEntity(ExternalBrandMapping mapping) {
        return ExternalBrandMappingJpaEntity.create(
                mapping.idValue(),
                mapping.externalSourceId(),
                mapping.externalBrandCode(),
                mapping.externalBrandName(),
                mapping.internalBrandId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }

    public ExternalBrandMapping toDomain(ExternalBrandMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = ExternalBrandMappingId.of(entity.getId());
        return ExternalBrandMapping.reconstitute(
                id,
                entity.getExternalSourceId(),
                entity.getExternalBrandCode(),
                entity.getExternalBrandName(),
                entity.getInternalBrandId(),
                ExternalBrandMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
