package com.ryuqq.marketplace.adapter.out.persistence.claim.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claim.mapper.ClaimShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claim.repository.ClaimShipmentJpaRepository;
import com.ryuqq.marketplace.application.claim.port.out.command.ClaimShipmentCommandPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import org.springframework.stereotype.Component;

/** 클레임 배송 Command Adapter. */
@Component
public class ClaimShipmentCommandAdapter implements ClaimShipmentCommandPort {

    private final ClaimShipmentJpaRepository repository;
    private final ClaimShipmentJpaEntityMapper mapper;

    public ClaimShipmentCommandAdapter(
            ClaimShipmentJpaRepository repository, ClaimShipmentJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ClaimShipment claimShipment) {
        repository.save(mapper.toEntity(claimShipment));
    }
}
