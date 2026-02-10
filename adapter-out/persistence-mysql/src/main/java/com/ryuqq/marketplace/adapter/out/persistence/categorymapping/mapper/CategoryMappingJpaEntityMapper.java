package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import org.springframework.stereotype.Component;

/** CategoryMapping JPA Entity Mapper. */
@Component
public class CategoryMappingJpaEntityMapper {

    public CategoryMappingJpaEntity toEntity(CategoryMapping categoryMapping) {
        return CategoryMappingJpaEntity.create(
                categoryMapping.idValue(),
                categoryMapping.salesChannelCategoryId(),
                categoryMapping.internalCategoryId(),
                categoryMapping.status().name(),
                categoryMapping.createdAt(),
                categoryMapping.updatedAt());
    }

    public CategoryMapping toDomain(CategoryMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = CategoryMappingId.of(entity.getId());
        return CategoryMapping.reconstitute(
                id,
                entity.getSalesChannelCategoryId(),
                entity.getInternalCategoryId(),
                CategoryMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
