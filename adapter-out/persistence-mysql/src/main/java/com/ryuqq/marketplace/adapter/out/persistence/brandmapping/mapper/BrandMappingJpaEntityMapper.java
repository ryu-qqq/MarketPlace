package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import org.springframework.stereotype.Component;

/** BrandMapping JPA Entity Mapper. */
@Component
public class BrandMappingJpaEntityMapper {

    public BrandMappingJpaEntity toEntity(BrandMapping brandMapping) {
        return BrandMappingJpaEntity.create(
                brandMapping.idValue(),
                brandMapping.salesChannelBrandId(),
                brandMapping.internalBrandId(),
                brandMapping.status().name(),
                brandMapping.createdAt(),
                brandMapping.updatedAt());
    }

    public BrandMapping toDomain(BrandMappingJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = BrandMappingId.of(entity.getId());
        return BrandMapping.reconstitute(
                id,
                entity.getSalesChannelBrandId(),
                entity.getInternalBrandId(),
                BrandMappingStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
