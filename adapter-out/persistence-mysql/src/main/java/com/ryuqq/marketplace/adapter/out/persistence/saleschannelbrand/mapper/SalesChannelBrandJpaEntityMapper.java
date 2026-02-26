package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import org.springframework.stereotype.Component;

/** SalesChannelBrand JPA Entity Mapper. */
@Component
public class SalesChannelBrandJpaEntityMapper {

    public SalesChannelBrandJpaEntity toEntity(SalesChannelBrand brand) {
        return SalesChannelBrandJpaEntity.create(
                brand.idValue(),
                brand.salesChannelId(),
                brand.externalBrandCode(),
                brand.externalBrandName(),
                brand.status().name(),
                brand.createdAt(),
                brand.updatedAt());
    }

    public SalesChannelBrand toDomain(SalesChannelBrandJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = SalesChannelBrandId.of(entity.getId());
        return SalesChannelBrand.reconstitute(
                id,
                entity.getSalesChannelId(),
                entity.getExternalBrandCode(),
                entity.getExternalBrandName(),
                SalesChannelBrandStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
