package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper.ShipmentOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentOutboxQueryPort;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 배송 아웃박스 Query Adapter. */
@Component
public class ShipmentOutboxQueryAdapter implements ShipmentOutboxQueryPort {

    private final ShipmentOutboxJpaRepository jpaRepository;
    private final ShipmentOutboxQueryDslRepository queryDslRepository;
    private final ShipmentOutboxJpaEntityMapper mapper;

    public ShipmentOutboxQueryAdapter(
            ShipmentOutboxJpaRepository jpaRepository,
            ShipmentOutboxQueryDslRepository queryDslRepository,
            ShipmentOutboxJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ShipmentOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryDslRepository.findPendingOutboxes(beforeTime, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ShipmentOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public ShipmentOutbox getById(Long outboxId) {
        return jpaRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "ShipmentOutbox를 찾을 수 없습니다. id=" + outboxId));
    }
}
