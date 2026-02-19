package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.ExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping JPA Entity Mapper. */
@Component
public class ExternalCategoryMappingJpaEntityMapper {

    public ExternalCategoryMappingJpaEntity toEntity(ExternalCategoryMapping mapping) {
        return ExternalCategoryMappingJpaEntity.create(
                mapping.idValue(),
                mapping.externalSourceId(),
                mapping.externalCategoryCode(),
                mapping.externalCategoryName(),
                mapping.internalCategoryId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }

    public ExternalCategoryMapping toDomain(ExternalCategoryMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = ExternalCategoryMappingId.of(entity.getId());
        return ExternalCategoryMapping.reconstitute(
                id,
                entity.getExternalSourceId(),
                entity.getExternalCategoryCode(),
                entity.getExternalCategoryName(),
                entity.getInternalCategoryId(),
                ExternalCategoryMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
