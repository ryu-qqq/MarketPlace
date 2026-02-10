package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper.SalesChannelBrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandQueryDslRepository;
import com.ryuqq.marketplace.application.saleschannelbrand.port.out.query.SalesChannelBrandQueryPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Query Adapter. */
@Component
public class SalesChannelBrandQueryAdapter implements SalesChannelBrandQueryPort {

    private final SalesChannelBrandQueryDslRepository repository;
    private final SalesChannelBrandJpaEntityMapper mapper;

    public SalesChannelBrandQueryAdapter(
            SalesChannelBrandQueryDslRepository repository,
            SalesChannelBrandJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SalesChannelBrand> findById(SalesChannelBrandId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SalesChannelBrand> findByCriteria(SalesChannelBrandSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(SalesChannelBrandSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalBrandCode) {
        return repository.existsBySalesChannelIdAndExternalCode(salesChannelId, externalBrandCode);
    }
}
