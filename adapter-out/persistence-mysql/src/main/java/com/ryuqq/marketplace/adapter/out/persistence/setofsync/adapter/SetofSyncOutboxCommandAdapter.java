package com.ryuqq.marketplace.adapter.out.persistence.setofsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.SetofSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.mapper.SetofSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.repository.SetofSyncOutboxJpaRepository;
import com.ryuqq.marketplace.application.setofsync.port.out.command.SetofSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import org.springframework.stereotype.Component;

@Component
public class SetofSyncOutboxCommandAdapter implements SetofSyncOutboxCommandPort {

    private final SetofSyncOutboxJpaRepository repository;
    private final SetofSyncOutboxJpaEntityMapper mapper;

    public SetofSyncOutboxCommandAdapter(
            SetofSyncOutboxJpaRepository repository, SetofSyncOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SetofSyncOutbox outbox) {
        SetofSyncOutboxJpaEntity entity = mapper.toEntity(outbox);
        SetofSyncOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }
}
