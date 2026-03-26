package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper.ExchangeOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeOutboxCommandPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 교환 아웃박스 Command Adapter. */
@Component
public class ExchangeOutboxCommandAdapter implements ExchangeOutboxCommandPort {

    private final ExchangeOutboxJpaRepository repository;
    private final ExchangeOutboxJpaEntityMapper mapper;

    public ExchangeOutboxCommandAdapter(
            ExchangeOutboxJpaRepository repository, ExchangeOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ExchangeOutbox outbox) {
        ExchangeOutboxJpaEntity saved = repository.save(mapper.toEntity(outbox));
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }

    @Override
    public void persistAll(List<ExchangeOutbox> outboxes) {
        List<ExchangeOutboxJpaEntity> entities = outboxes.stream().map(mapper::toEntity).toList();
        List<ExchangeOutboxJpaEntity> savedEntities = repository.saveAll(entities);
        for (int i = 0; i < outboxes.size(); i++) {
            outboxes.get(i).refreshVersion(savedEntities.get(i).getVersion());
        }
    }
}
