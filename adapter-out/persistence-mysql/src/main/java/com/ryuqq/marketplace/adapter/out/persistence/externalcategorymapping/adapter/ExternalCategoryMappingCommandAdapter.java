package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.ExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.mapper.ExternalCategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.repository.ExternalCategoryMappingJpaRepository;
import com.ryuqq.marketplace.application.externalcategorymapping.port.out.command.ExternalCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Command Adapter. */
@Component
public class ExternalCategoryMappingCommandAdapter implements ExternalCategoryMappingCommandPort {

    private final ExternalCategoryMappingJpaRepository repository;
    private final ExternalCategoryMappingJpaEntityMapper mapper;

    public ExternalCategoryMappingCommandAdapter(
            ExternalCategoryMappingJpaRepository repository,
            ExternalCategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ExternalCategoryMapping mapping) {
        ExternalCategoryMappingJpaEntity entity = mapper.toEntity(mapping);
        ExternalCategoryMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<ExternalCategoryMapping> mappings) {
        List<ExternalCategoryMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream()
                .map(ExternalCategoryMappingJpaEntity::getId)
                .toList();
    }
}
