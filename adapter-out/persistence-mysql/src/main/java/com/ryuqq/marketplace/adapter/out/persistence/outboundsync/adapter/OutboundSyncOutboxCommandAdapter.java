package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.application.outboundsync.port.out.command.OutboundSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 외부 상품 연동 Outbox 커맨드 어댑터. */
@Component
public class OutboundSyncOutboxCommandAdapter implements OutboundSyncOutboxCommandPort {

    private final OutboundSyncOutboxJpaRepository repository;
    private final OutboundSyncOutboxJpaEntityMapper mapper;

    public OutboundSyncOutboxCommandAdapter(
            OutboundSyncOutboxJpaRepository repository, OutboundSyncOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(OutboundSyncOutbox outbox) {
        OutboundSyncOutboxJpaEntity entity = mapper.toEntity(outbox);
        OutboundSyncOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public void persistAll(List<OutboundSyncOutbox> outboxes) {
        List<OutboundSyncOutboxJpaEntity> entities =
                outboxes.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
