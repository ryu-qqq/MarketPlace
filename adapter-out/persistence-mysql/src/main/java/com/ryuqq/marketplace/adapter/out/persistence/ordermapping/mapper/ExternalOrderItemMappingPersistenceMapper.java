package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity.ExternalOrderItemMappingJpaEntity;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.ordermapping.id.ExternalOrderItemMappingId;
import org.springframework.stereotype.Component;

/** 외부 주문상품 매핑 Persistence Mapper. */
@Component
public class ExternalOrderItemMappingPersistenceMapper {

    public ExternalOrderItemMappingJpaEntity toEntity(ExternalOrderItemMapping mapping) {
        return ExternalOrderItemMappingJpaEntity.create(
                null,
                mapping.salesChannelId(),
                mapping.channelCode(),
                mapping.externalOrderId(),
                mapping.externalProductOrderId(),
                mapping.orderItemId().value(),
                mapping.createdAt());
    }

    public ExternalOrderItemMapping toDomain(ExternalOrderItemMappingJpaEntity entity) {
        return ExternalOrderItemMapping.reconstitute(
                ExternalOrderItemMappingId.of(entity.getId()),
                entity.getSalesChannelId(),
                entity.getChannelCode(),
                entity.getExternalOrderId(),
                entity.getExternalProductOrderId(),
                OrderItemId.of(entity.getOrderItemId()),
                entity.getCreatedAt());
    }
}
