package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderIdMappingJpaRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyOrderIdMappingCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderIdMappingCommandAdapter - 레거시 주문 ID 매핑 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyOrderIdMappingCommandAdapter implements LegacyOrderIdMappingCommandPort {

    private final LegacyOrderIdMappingJpaRepository repository;
    private final LegacyOrderIdMappingJpaEntityMapper mapper;

    public LegacyOrderIdMappingCommandAdapter(
            LegacyOrderIdMappingJpaRepository repository,
            LegacyOrderIdMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyOrderIdMapping mapping) {
        LegacyOrderIdMappingJpaEntity entity = mapper.toEntity(mapping);
        LegacyOrderIdMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<LegacyOrderIdMapping> mappings) {
        List<LegacyOrderIdMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        List<LegacyOrderIdMappingJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(LegacyOrderIdMappingJpaEntity::getId).toList();
    }
}
