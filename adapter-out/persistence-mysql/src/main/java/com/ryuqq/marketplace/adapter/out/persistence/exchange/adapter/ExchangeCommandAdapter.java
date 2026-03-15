package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeItemJpaRepository;
import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeCommandPort;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.springframework.stereotype.Component;

/** 교환 클레임 Command Adapter. */
@Component
public class ExchangeCommandAdapter implements ExchangeCommandPort {

    private final ExchangeClaimJpaRepository claimRepository;
    private final ExchangeItemJpaRepository itemRepository;
    private final ExchangePersistenceMapper mapper;

    public ExchangeCommandAdapter(
            ExchangeClaimJpaRepository claimRepository,
            ExchangeItemJpaRepository itemRepository,
            ExchangePersistenceMapper mapper) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ExchangeClaim exchangeClaim) {
        claimRepository.save(mapper.toEntity(exchangeClaim));
        List<ExchangeItemJpaEntity> itemEntities =
                exchangeClaim.exchangeItems().stream()
                        .map(item -> mapper.toItemEntity(item, exchangeClaim.idValue()))
                        .toList();
        itemRepository.saveAll(itemEntities);
    }
}
