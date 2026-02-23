package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper.InboundBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository.InboundBrandMappingJpaRepository;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.command.InboundBrandMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundBrandMapping Command Adapter. */
@Component
public class InboundBrandMappingCommandAdapter implements InboundBrandMappingCommandPort {

    private final InboundBrandMappingJpaRepository repository;
    private final InboundBrandMappingJpaEntityMapper mapper;

    public InboundBrandMappingCommandAdapter(
            InboundBrandMappingJpaRepository repository,
            InboundBrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(InboundBrandMapping mapping) {
        InboundBrandMappingJpaEntity entity = mapper.toEntity(mapping);
        InboundBrandMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<InboundBrandMapping> mappings) {
        List<InboundBrandMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream()
                .map(InboundBrandMappingJpaEntity::getId)
                .toList();
    }
}
