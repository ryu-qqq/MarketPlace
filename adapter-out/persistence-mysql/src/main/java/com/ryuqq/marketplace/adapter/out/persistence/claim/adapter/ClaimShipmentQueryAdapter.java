package com.ryuqq.marketplace.adapter.out.persistence.claim.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claim.mapper.ClaimShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claim.repository.ClaimShipmentJpaRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 클레임 배송 Query Adapter. */
@Component
public class ClaimShipmentQueryAdapter implements ClaimShipmentQueryPort {

    private final ClaimShipmentJpaRepository repository;
    private final ClaimShipmentJpaEntityMapper mapper;

    public ClaimShipmentQueryAdapter(
            ClaimShipmentJpaRepository repository, ClaimShipmentJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ClaimShipment> findById(ClaimShipmentId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }
}
