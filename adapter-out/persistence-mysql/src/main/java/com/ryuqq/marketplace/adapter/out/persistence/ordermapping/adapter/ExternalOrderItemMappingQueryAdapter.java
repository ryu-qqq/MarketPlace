package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.mapper.ExternalOrderItemMappingPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository.ExternalOrderItemMappingQueryDslRepository;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 외부 주문상품 매핑 Query Adapter. */
@Component
public class ExternalOrderItemMappingQueryAdapter implements ExternalOrderItemMappingQueryPort {

    private final ExternalOrderItemMappingQueryDslRepository queryDslRepository;
    private final ExternalOrderItemMappingPersistenceMapper mapper;

    public ExternalOrderItemMappingQueryAdapter(
            ExternalOrderItemMappingQueryDslRepository queryDslRepository,
            ExternalOrderItemMappingPersistenceMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ExternalOrderItemMapping> findBySalesChannelIdAndExternalProductOrderId(
            long salesChannelId, String externalProductOrderId) {
        return queryDslRepository
                .findBySalesChannelIdAndExternalProductOrderId(
                        salesChannelId, externalProductOrderId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ExternalOrderItemMapping> findByOrderItemIds(List<OrderItemId> orderItemIds) {
        List<String> ids = orderItemIds.stream().map(OrderItemId::value).toList();
        return queryDslRepository.findByOrderItemIdIn(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ExternalOrderItemMapping> findByOrderItemId(String orderItemId) {
        return queryDslRepository.findByOrderItemIdIn(List.of(orderItemId)).stream()
                .findFirst()
                .map(mapper::toDomain);
    }
}
