package com.ryuqq.marketplace.adapter.out.persistence.shipment.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper.ShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentQueryDslRepository;
import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentQueryPort;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Shipment Query Adapter. */
@Component
public class ShipmentQueryAdapter implements ShipmentQueryPort {

    private final ShipmentQueryDslRepository repository;
    private final ShipmentJpaEntityMapper mapper;

    public ShipmentQueryAdapter(
            ShipmentQueryDslRepository repository, ShipmentJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Shipment> findById(ShipmentId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrderItemId(OrderItemId orderItemId) {
        return repository.findByOrderItemId(orderItemId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Shipment> findByOrderItemIds(List<OrderItemId> orderItemIds) {
        List<String> ids = orderItemIds.stream().map(OrderItemId::value).toList();
        return repository.findByOrderItemIds(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Shipment> findByCriteria(ShipmentSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ShipmentSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public Map<ShipmentStatus, Long> countByStatus() {
        Map<String, Long> rawCounts = repository.countByStatus();
        Map<ShipmentStatus, Long> result = new EnumMap<>(ShipmentStatus.class);
        for (Map.Entry<String, Long> entry : rawCounts.entrySet()) {
            result.put(ShipmentStatus.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }
}
