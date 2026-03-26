package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper.CancelOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.cancel.port.out.query.CancelOutboxQueryPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 취소 아웃박스 Query Adapter. */
@Component
public class CancelOutboxQueryAdapter implements CancelOutboxQueryPort {

    private final CancelOutboxQueryDslRepository queryDslRepository;
    private final CancelOutboxJpaRepository jpaRepository;
    private final CancelOutboxJpaEntityMapper mapper;

    public CancelOutboxQueryAdapter(
            CancelOutboxQueryDslRepository queryDslRepository,
            CancelOutboxJpaRepository jpaRepository,
            CancelOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CancelOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryDslRepository.findPendingOutboxes(beforeTime, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CancelOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CancelOutbox getById(Long outboxId) {
        return jpaRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () -> new IllegalStateException("CancelOutbox를 찾을 수 없습니다. id=" + outboxId));
    }
}
