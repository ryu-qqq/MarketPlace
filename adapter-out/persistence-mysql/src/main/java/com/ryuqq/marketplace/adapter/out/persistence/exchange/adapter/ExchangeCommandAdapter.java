package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeCommandPort;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.springframework.stereotype.Component;

/** 교환 클레임 Command Adapter. */
@Component
public class ExchangeCommandAdapter implements ExchangeCommandPort {

    private final ExchangeClaimJpaRepository claimRepository;
    private final ExchangePersistenceMapper mapper;

    public ExchangeCommandAdapter(
            ExchangeClaimJpaRepository claimRepository,
            ExchangePersistenceMapper mapper) {
        this.claimRepository = claimRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ExchangeClaim exchangeClaim) {
        claimRepository.save(mapper.toEntity(exchangeClaim));
    }

    @Override
    public void persistAll(List<ExchangeClaim> exchangeClaims) {
        var entities = exchangeClaims.stream().map(mapper::toEntity).toList();
        claimRepository.saveAll(entities);
    }
}
