package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.mapper.CategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingJpaRepository;
import com.ryuqq.marketplace.application.categorymapping.port.out.command.CategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryMapping Command Adapter. */
@Component
public class CategoryMappingCommandAdapter implements CategoryMappingCommandPort {

    private final CategoryMappingJpaRepository repository;
    private final CategoryMappingJpaEntityMapper mapper;

    public CategoryMappingCommandAdapter(
            CategoryMappingJpaRepository repository, CategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(CategoryMapping categoryMapping) {
        CategoryMappingJpaEntity entity = mapper.toEntity(categoryMapping);
        CategoryMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<CategoryMapping> categoryMappings) {
        List<CategoryMappingJpaEntity> entities =
                categoryMappings.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream().map(CategoryMappingJpaEntity::getId).toList();
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
