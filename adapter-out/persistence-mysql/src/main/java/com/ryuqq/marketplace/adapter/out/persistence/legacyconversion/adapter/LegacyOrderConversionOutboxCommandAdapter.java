package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderConversionOutboxJpaRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyOrderConversionOutboxCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderConversionOutboxCommandAdapter - 레거시 주문 변환 Outbox 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyOrderConversionOutboxCommandAdapter
        implements LegacyOrderConversionOutboxCommandPort {

    private final LegacyOrderConversionOutboxJpaRepository repository;
    private final LegacyOrderConversionOutboxJpaEntityMapper mapper;

    public LegacyOrderConversionOutboxCommandAdapter(
            LegacyOrderConversionOutboxJpaRepository repository,
            LegacyOrderConversionOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyOrderConversionOutbox outbox) {
        LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(outbox);
        LegacyOrderConversionOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<LegacyOrderConversionOutbox> outboxes) {
        List<LegacyOrderConversionOutboxJpaEntity> entities =
                outboxes.stream().map(mapper::toEntity).toList();
        List<LegacyOrderConversionOutboxJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(LegacyOrderConversionOutboxJpaEntity::getId).toList();
    }
}
