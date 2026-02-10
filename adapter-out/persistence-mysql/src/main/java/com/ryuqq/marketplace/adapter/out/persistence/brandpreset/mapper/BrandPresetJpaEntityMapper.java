package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.vo.BrandPresetStatus;
import org.springframework.stereotype.Component;

/** BrandPreset JPA Entity Mapper. */
@Component
public class BrandPresetJpaEntityMapper {

    public BrandPresetJpaEntity toEntity(BrandPreset brandPreset) {
        return BrandPresetJpaEntity.create(
                brandPreset.idValue(),
                brandPreset.shopId(),
                brandPreset.salesChannelBrandId(),
                brandPreset.presetName(),
                brandPreset.status().name(),
                brandPreset.createdAt(),
                brandPreset.updatedAt());
    }

    public BrandPreset toDomain(BrandPresetJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = BrandPresetId.of(entity.getId());
        return BrandPreset.reconstitute(
                id,
                entity.getShopId(),
                entity.getSalesChannelBrandId(),
                entity.getPresetName(),
                BrandPresetStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
