package com.ryuqq.marketplace.adapter.out.persistence.category.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.category.mapper.CategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryQueryDslRepository;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Category Query Adapter. */
@Component
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryQueryDslRepository repository;
    private final CategoryJpaEntityMapper mapper;

    public CategoryQueryAdapter(
            CategoryQueryDslRepository repository, CategoryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Category> findByCriteria(CategorySearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(CategorySearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public List<Category> findAllByIds(List<Long> ids) {
        return repository.findAllByIds(ids).stream().map(mapper::toDomain).toList();
    }
}
