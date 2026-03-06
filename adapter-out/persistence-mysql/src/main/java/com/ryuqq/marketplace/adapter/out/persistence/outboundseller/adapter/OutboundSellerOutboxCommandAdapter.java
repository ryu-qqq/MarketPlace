package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.OutboundSellerOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.mapper.OutboundSellerOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.repository.OutboundSellerOutboxJpaRepository;
import com.ryuqq.marketplace.application.outboundseller.port.out.command.OutboundSellerOutboxCommandPort;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import org.springframework.stereotype.Component;

@Component
public class OutboundSellerOutboxCommandAdapter implements OutboundSellerOutboxCommandPort {

    private final OutboundSellerOutboxJpaRepository repository;
    private final OutboundSellerOutboxJpaEntityMapper mapper;

    public OutboundSellerOutboxCommandAdapter(
            OutboundSellerOutboxJpaRepository repository,
            OutboundSellerOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(OutboundSellerOutbox outbox) {
        OutboundSellerOutboxJpaEntity entity = mapper.toEntity(outbox);
        OutboundSellerOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }
}
