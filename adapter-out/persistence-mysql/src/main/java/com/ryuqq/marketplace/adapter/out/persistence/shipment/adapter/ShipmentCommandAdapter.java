package com.ryuqq.marketplace.adapter.out.persistence.shipment.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper.ShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentCommandPort;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shipment Command Adapter. */
@Component
public class ShipmentCommandAdapter implements ShipmentCommandPort {

    private final ShipmentJpaRepository repository;
    private final ShipmentJpaEntityMapper mapper;

    public ShipmentCommandAdapter(
            ShipmentJpaRepository repository, ShipmentJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(Shipment shipment) {
        repository.save(mapper.toEntity(shipment));
    }

    @Override
    public void persistAll(List<Shipment> shipments) {
        repository.saveAll(shipments.stream().map(mapper::toEntity).toList());
    }
}
