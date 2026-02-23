package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.IntelligenceOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.IntelligenceOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.IntelligenceOutboxQueryPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * IntelligenceOutboxQueryAdapter - Intelligence Pipeline Outbox 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class IntelligenceOutboxQueryAdapter implements IntelligenceOutboxQueryPort {

    private final IntelligenceOutboxQueryDslRepository queryDslRepository;
    private final IntelligenceOutboxJpaEntityMapper mapper;

    public IntelligenceOutboxQueryAdapter(
            IntelligenceOutboxQueryDslRepository queryDslRepository,
            IntelligenceOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<IntelligenceOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<IntelligenceOutbox> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<IntelligenceOutbox> findById(Long outboxId) {
        return queryDslRepository.findById(outboxId).map(mapper::toDomain);
    }
}
