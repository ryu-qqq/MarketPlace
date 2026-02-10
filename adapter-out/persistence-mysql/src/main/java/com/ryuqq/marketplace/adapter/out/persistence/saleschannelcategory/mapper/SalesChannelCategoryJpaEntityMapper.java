package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import org.springframework.stereotype.Component;

/** SalesChannelCategory JPA Entity Mapper. */
@Component
public class SalesChannelCategoryJpaEntityMapper {

    public SalesChannelCategoryJpaEntity toEntity(SalesChannelCategory category) {
        return SalesChannelCategoryJpaEntity.create(
                category.idValue(),
                category.salesChannelId(),
                category.externalCategoryCode(),
                category.externalCategoryName(),
                category.parentId(),
                category.depth(),
                category.path(),
                category.sortOrder(),
                category.isLeaf(),
                category.status().name(),
                category.displayPath(),
                category.createdAt(),
                category.updatedAt());
    }

    public SalesChannelCategory toDomain(SalesChannelCategoryJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = SalesChannelCategoryId.of(entity.getId());
        return SalesChannelCategory.reconstitute(
                id,
                entity.getSalesChannelId(),
                entity.getExternalCategoryCode(),
                entity.getExternalCategoryName(),
                entity.getParentId(),
                entity.getDepth(),
                entity.getPath(),
                entity.getSortOrder(),
                entity.isLeaf(),
                SalesChannelCategoryStatus.fromString(entity.getStatus()),
                entity.getDisplayPath(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
