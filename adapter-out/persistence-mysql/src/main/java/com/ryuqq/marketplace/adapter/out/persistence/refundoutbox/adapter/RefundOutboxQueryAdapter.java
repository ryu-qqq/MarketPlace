package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.mapper.RefundOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.refund.port.out.query.RefundOutboxQueryPort;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** 환불 아웃박스 Query Adapter. */
@Component
public class RefundOutboxQueryAdapter implements RefundOutboxQueryPort {

    private final RefundOutboxQueryDslRepository queryDslRepository;
    private final RefundOutboxJpaRepository jpaRepository;
    private final RefundOutboxJpaEntityMapper mapper;

    public RefundOutboxQueryAdapter(
            RefundOutboxQueryDslRepository queryDslRepository,
            RefundOutboxJpaRepository jpaRepository,
            RefundOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RefundOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryDslRepository.findPendingOutboxes(beforeTime, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RefundOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public RefundOutbox getById(Long outboxId) {
        return jpaRepository
                .findById(outboxId)
                .map(mapper::toDomain)
                .orElseThrow(
                        () -> new IllegalStateException("RefundOutbox를 찾을 수 없습니다. id=" + outboxId));
    }
}
