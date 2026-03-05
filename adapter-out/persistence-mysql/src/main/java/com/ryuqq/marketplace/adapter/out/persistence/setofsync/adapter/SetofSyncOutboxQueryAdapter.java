package com.ryuqq.marketplace.adapter.out.persistence.setofsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.setofsync.mapper.SetofSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.repository.SetofSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.setofsync.port.out.query.SetofSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class SetofSyncOutboxQueryAdapter implements SetofSyncOutboxQueryPort {

    private final SetofSyncOutboxQueryDslRepository queryDslRepository;
    private final SetofSyncOutboxJpaEntityMapper mapper;

    public SetofSyncOutboxQueryAdapter(
            SetofSyncOutboxQueryDslRepository queryDslRepository,
            SetofSyncOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public SetofSyncOutbox getById(Long outboxId) {
        return queryDslRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "SetofSyncOutbox를 찾을 수 없습니다. id=" + outboxId));
    }

    @Override
    public List<SetofSyncOutbox> findPendingForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SetofSyncOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeout(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
