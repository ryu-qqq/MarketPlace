package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyProductIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyProductIdMappingJpaRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyProductIdMappingCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyProductIdMappingCommandAdapter - ID 매핑 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyProductIdMappingCommandAdapter implements LegacyProductIdMappingCommandPort {

    private final LegacyProductIdMappingJpaRepository repository;
    private final LegacyProductIdMappingJpaEntityMapper mapper;

    public LegacyProductIdMappingCommandAdapter(
            LegacyProductIdMappingJpaRepository repository,
            LegacyProductIdMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyProductIdMapping mapping) {
        LegacyProductIdMappingJpaEntity entity = mapper.toEntity(mapping);
        LegacyProductIdMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public void persistAll(List<LegacyProductIdMapping> mappings) {
        List<LegacyProductIdMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
