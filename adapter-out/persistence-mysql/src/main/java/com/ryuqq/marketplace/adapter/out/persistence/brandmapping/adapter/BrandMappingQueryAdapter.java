package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.mapper.BrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository.BrandMappingQueryDslRepository;
import com.ryuqq.marketplace.application.brandmapping.port.out.query.BrandMappingQueryPort;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** BrandMapping Query Adapter. */
@Component
public class BrandMappingQueryAdapter implements BrandMappingQueryPort {

    private final BrandMappingQueryDslRepository repository;
    private final BrandMappingJpaEntityMapper mapper;

    public BrandMappingQueryAdapter(
            BrandMappingQueryDslRepository repository, BrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<BrandMapping> findById(BrandMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<BrandMapping> findByCriteria(BrandMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(BrandMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsBySalesChannelBrandId(Long salesChannelBrandId) {
        return repository.existsBySalesChannelBrandId(salesChannelBrandId);
    }
}
