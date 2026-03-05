package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.mapper.OutboundSellerOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.repository.OutboundSellerOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.outboundseller.port.out.query.OutboundSellerOutboxQueryPort;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class OutboundSellerOutboxQueryAdapter implements OutboundSellerOutboxQueryPort {

    private final OutboundSellerOutboxQueryDslRepository queryDslRepository;
    private final OutboundSellerOutboxJpaEntityMapper mapper;

    public OutboundSellerOutboxQueryAdapter(
            OutboundSellerOutboxQueryDslRepository queryDslRepository,
            OutboundSellerOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public OutboundSellerOutbox getById(Long outboxId) {
        return queryDslRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "OutboundSellerOutbox를 찾을 수 없습니다. id=" + outboxId));
    }

    @Override
    public List<OutboundSellerOutbox> findPendingForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OutboundSellerOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeout(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
