package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper.SalesChannelCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryQueryDslRepository;
import com.ryuqq.marketplace.application.saleschannelcategory.port.out.query.SalesChannelCategoryQueryPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Query Adapter. */
@Component
public class SalesChannelCategoryQueryAdapter implements SalesChannelCategoryQueryPort {

    private final SalesChannelCategoryQueryDslRepository repository;
    private final SalesChannelCategoryJpaEntityMapper mapper;

    public SalesChannelCategoryQueryAdapter(
            SalesChannelCategoryQueryDslRepository repository,
            SalesChannelCategoryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SalesChannelCategory> findById(SalesChannelCategoryId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SalesChannelCategory> findByCriteria(SalesChannelCategorySearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(SalesChannelCategorySearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalCategoryCode) {
        return repository.existsBySalesChannelIdAndExternalCode(
                salesChannelId, externalCategoryCode);
    }
}
