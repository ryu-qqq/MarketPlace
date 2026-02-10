package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.vo.CategoryPresetStatus;
import org.springframework.stereotype.Component;

/** CategoryPreset JPA Entity Mapper. */
@Component
public class CategoryPresetJpaEntityMapper {

    public CategoryPresetJpaEntity toEntity(CategoryPreset categoryPreset) {
        return CategoryPresetJpaEntity.create(
                categoryPreset.idValue(),
                categoryPreset.shopId(),
                categoryPreset.salesChannelCategoryId(),
                categoryPreset.presetName(),
                categoryPreset.status().name(),
                categoryPreset.createdAt(),
                categoryPreset.updatedAt());
    }

    public CategoryPreset toDomain(CategoryPresetJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = CategoryPresetId.of(entity.getId());
        return CategoryPreset.reconstitute(
                id,
                entity.getShopId(),
                entity.getSalesChannelCategoryId(),
                entity.getPresetName(),
                CategoryPresetStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
