package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper.CategoryPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetJpaRepository;
import com.ryuqq.marketplace.application.categorypreset.port.out.command.CategoryPresetCommandPort;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryPreset Command Adapter. */
@Component
public class CategoryPresetCommandAdapter implements CategoryPresetCommandPort {

    private final CategoryPresetJpaRepository repository;
    private final CategoryPresetJpaEntityMapper mapper;

    public CategoryPresetCommandAdapter(
            CategoryPresetJpaRepository repository, CategoryPresetJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(CategoryPreset categoryPreset) {
        CategoryPresetJpaEntity entity = mapper.toEntity(categoryPreset);
        CategoryPresetJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<CategoryPreset> categoryPresets) {
        List<CategoryPresetJpaEntity> entities =
                categoryPresets.stream().map(mapper::toEntity).toList();
        List<CategoryPresetJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(CategoryPresetJpaEntity::getId).toList();
    }
}
