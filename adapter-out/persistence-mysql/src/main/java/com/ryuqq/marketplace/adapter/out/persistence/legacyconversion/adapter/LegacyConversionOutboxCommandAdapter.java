package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyConversionOutboxJpaRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyConversionOutboxCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import org.springframework.stereotype.Component;

/**
 * LegacyConversionOutboxCommandAdapter - 레거시 변환 Outbox 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyConversionOutboxCommandAdapter implements LegacyConversionOutboxCommandPort {

    private final LegacyConversionOutboxJpaRepository repository;
    private final LegacyConversionOutboxJpaEntityMapper mapper;

    public LegacyConversionOutboxCommandAdapter(
            LegacyConversionOutboxJpaRepository repository,
            LegacyConversionOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyConversionOutbox outbox) {
        LegacyConversionOutboxJpaEntity entity = mapper.toEntity(outbox);
        LegacyConversionOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
