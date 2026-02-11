package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper.CategoryPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetQueryDslRepository;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetQueryPort;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** CategoryPreset Query Adapter. */
@Component
public class CategoryPresetQueryAdapter implements CategoryPresetQueryPort {

    private final CategoryPresetQueryDslRepository repository;
    private final CategoryPresetJpaEntityMapper mapper;

    public CategoryPresetQueryAdapter(
            CategoryPresetQueryDslRepository repository, CategoryPresetJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CategoryPreset> findById(CategoryPresetId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<CategoryPresetResult> findByCriteria(CategoryPresetSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toResult).toList();
    }

    @Override
    public long countByCriteria(CategoryPresetSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public List<CategoryPreset> findAllByIds(List<Long> ids) {
        return repository.findAllByIds(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Long> findSalesChannelCategoryIdByCode(
            Long salesChannelId, String categoryCode) {
        return repository.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);
    }
}
