package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.mapper.BrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository.BrandMappingJpaRepository;
import com.ryuqq.marketplace.application.brandmapping.port.out.command.BrandMappingCommandPort;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandMapping Command Adapter. */
@Component
public class BrandMappingCommandAdapter implements BrandMappingCommandPort {

    private final BrandMappingJpaRepository repository;
    private final BrandMappingJpaEntityMapper mapper;

    public BrandMappingCommandAdapter(
            BrandMappingJpaRepository repository, BrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(BrandMapping brandMapping) {
        BrandMappingJpaEntity entity = mapper.toEntity(brandMapping);
        BrandMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<BrandMapping> brandMappings) {
        List<BrandMappingJpaEntity> entities =
                brandMappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream().map(BrandMappingJpaEntity::getId).toList();
    }

    @Override
    public void deleteAllByPresetId(Long presetId) {
        repository.deleteAllByPresetId(presetId);
    }

    @Override
    public void deleteAllByPresetIds(List<Long> presetIds) {
        repository.deleteAllByPresetIdIn(presetIds);
    }
}
