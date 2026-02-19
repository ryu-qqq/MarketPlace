package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.ExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.mapper.ExternalBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.repository.ExternalBrandMappingJpaRepository;
import com.ryuqq.marketplace.application.externalbrandmapping.port.out.command.ExternalBrandMappingCommandPort;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Command Adapter. */
@Component
public class ExternalBrandMappingCommandAdapter implements ExternalBrandMappingCommandPort {

    private final ExternalBrandMappingJpaRepository repository;
    private final ExternalBrandMappingJpaEntityMapper mapper;

    public ExternalBrandMappingCommandAdapter(
            ExternalBrandMappingJpaRepository repository,
            ExternalBrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ExternalBrandMapping mapping) {
        ExternalBrandMappingJpaEntity entity = mapper.toEntity(mapping);
        ExternalBrandMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<ExternalBrandMapping> mappings) {
        List<ExternalBrandMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream()
                .map(ExternalBrandMappingJpaEntity::getId)
                .toList();
    }
}
