package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundproduct.id.OutboundProductId;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductJpaEntityMapper {

    public OutboundProductJpaEntity toEntity(OutboundProduct product) {
        return OutboundProductJpaEntity.create(
                product.idValue(),
                product.productGroupIdValue(),
                product.salesChannelIdValue(),
                product.externalProductId(),
                product.status().name(),
                product.createdAt(),
                product.updatedAt());
    }

    public OutboundProduct toDomain(OutboundProductJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        return OutboundProduct.reconstitute(
                OutboundProductId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                SalesChannelId.of(entity.getSalesChannelId()),
                entity.getExternalProductId(),
                OutboundProductStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
