package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.mapper.CategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.application.categorymapping.port.out.query.CategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** CategoryMapping Query Adapter. */
@Component
public class CategoryMappingQueryAdapter implements CategoryMappingQueryPort {

    private final CategoryMappingQueryDslRepository repository;
    private final CategoryMappingJpaEntityMapper mapper;

    public CategoryMappingQueryAdapter(
            CategoryMappingQueryDslRepository repository, CategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CategoryMapping> findById(CategoryMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<CategoryMapping> findByCriteria(CategoryMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(CategoryMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsBySalesChannelCategoryId(Long salesChannelCategoryId) {
        return repository.existsBySalesChannelCategoryId(salesChannelCategoryId);
    }
}
