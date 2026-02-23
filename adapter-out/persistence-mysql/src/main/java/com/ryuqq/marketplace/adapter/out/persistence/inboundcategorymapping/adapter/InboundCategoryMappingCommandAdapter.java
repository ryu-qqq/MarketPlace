package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.mapper.InboundCategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository.InboundCategoryMappingJpaRepository;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.command.InboundCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping Command Adapter. */
@Component
public class InboundCategoryMappingCommandAdapter implements InboundCategoryMappingCommandPort {

    private final InboundCategoryMappingJpaRepository repository;
    private final InboundCategoryMappingJpaEntityMapper mapper;

    public InboundCategoryMappingCommandAdapter(
            InboundCategoryMappingJpaRepository repository,
            InboundCategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(InboundCategoryMapping mapping) {
        InboundCategoryMappingJpaEntity entity = mapper.toEntity(mapping);
        InboundCategoryMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<InboundCategoryMapping> mappings) {
        List<InboundCategoryMappingJpaEntity> entities =
                mappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream()
                .map(InboundCategoryMappingJpaEntity::getId)
                .toList();
    }
}
