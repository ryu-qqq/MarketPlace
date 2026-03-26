package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementJpaRepository;
import com.ryuqq.marketplace.application.settlement.port.out.command.SettlementCommandPort;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import org.springframework.stereotype.Component;

/** 정산 Command Adapter. */
@Component
public class SettlementCommandAdapter implements SettlementCommandPort {

    private final SettlementJpaRepository repository;
    private final SettlementJpaEntityMapper mapper;

    public SettlementCommandAdapter(
            SettlementJpaRepository repository, SettlementJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(Settlement settlement) {
        repository.save(mapper.toEntity(settlement));
    }
}
