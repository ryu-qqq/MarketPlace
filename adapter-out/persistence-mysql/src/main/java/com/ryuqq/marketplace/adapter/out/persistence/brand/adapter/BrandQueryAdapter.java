package com.ryuqq.marketplace.adapter.out.persistence.brand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brand.mapper.BrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandQueryDslRepository;
import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Brand Query Adapter. */
@Component
public class BrandQueryAdapter implements BrandQueryPort {

    private final BrandQueryDslRepository repository;
    private final BrandJpaEntityMapper mapper;

    public BrandQueryAdapter(BrandQueryDslRepository repository, BrandJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Brand> findById(BrandId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Brand> findByCriteria(BrandSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(BrandSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public List<Brand> findAllByIds(List<Long> ids) {
        return repository.findAllByIds(ids).stream().map(mapper::toDomain).toList();
    }
}
