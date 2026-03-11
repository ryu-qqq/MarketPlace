package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper.ShipmentOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.application.shipment.port.out.command.ShipmentOutboxCommandPort;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 배송 아웃박스 Command Adapter. */
@Component
public class ShipmentOutboxCommandAdapter implements ShipmentOutboxCommandPort {

    private final ShipmentOutboxJpaRepository repository;
    private final ShipmentOutboxJpaEntityMapper mapper;

    public ShipmentOutboxCommandAdapter(
            ShipmentOutboxJpaRepository repository, ShipmentOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ShipmentOutbox outbox) {
        ShipmentOutboxJpaEntity entity = mapper.toEntity(outbox);
        ShipmentOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }

    @Override
    public void persistAll(List<ShipmentOutbox> outboxes) {
        List<ShipmentOutboxJpaEntity> entities = outboxes.stream().map(mapper::toEntity).toList();
        List<ShipmentOutboxJpaEntity> savedEntities = repository.saveAll(entities);
        for (int i = 0; i < outboxes.size(); i++) {
            outboxes.get(i).refreshVersion(savedEntities.get(i).getVersion());
        }
    }
}
