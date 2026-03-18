package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper.ExchangeOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeOutboxQueryPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 교환 아웃박스 Query Adapter. */
@Component
public class ExchangeOutboxQueryAdapter implements ExchangeOutboxQueryPort {

    private final ExchangeOutboxQueryDslRepository queryDslRepository;
    private final ExchangeOutboxJpaRepository jpaRepository;
    private final ExchangeOutboxJpaEntityMapper mapper;

    public ExchangeOutboxQueryAdapter(
            ExchangeOutboxQueryDslRepository queryDslRepository,
            ExchangeOutboxJpaRepository jpaRepository,
            ExchangeOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ExchangeOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryDslRepository.findPendingOutboxes(beforeTime, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExchangeOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryDslRepository
                .findProcessingTimeoutOutboxes(timeoutBefore, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public ExchangeOutbox getById(Long outboxId) {
        return jpaRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "ExchangeOutbox를 찾을 수 없습니다. id=" + outboxId));
    }
}
