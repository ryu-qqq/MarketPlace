package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.IntelligenceOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.IntelligenceOutboxJpaRepository;
import com.ryuqq.marketplace.application.productintelligence.port.out.command.IntelligenceOutboxCommandPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import org.springframework.stereotype.Component;

/**
 * IntelligenceOutboxCommandAdapter - Intelligence Pipeline Outbox 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class IntelligenceOutboxCommandAdapter implements IntelligenceOutboxCommandPort {

    private final IntelligenceOutboxJpaRepository repository;
    private final IntelligenceOutboxJpaEntityMapper mapper;

    public IntelligenceOutboxCommandAdapter(
            IntelligenceOutboxJpaRepository repository, IntelligenceOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(IntelligenceOutbox outbox) {
        IntelligenceOutboxJpaEntity entity = mapper.toEntity(outbox);
        IntelligenceOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
