package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper.SalesChannelCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
import com.ryuqq.marketplace.application.saleschannelcategory.port.out.command.SalesChannelCategoryCommandPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Command Adapter. */
@Component
public class SalesChannelCategoryCommandAdapter implements SalesChannelCategoryCommandPort {

    private final SalesChannelCategoryJpaRepository repository;
    private final SalesChannelCategoryJpaEntityMapper mapper;

    public SalesChannelCategoryCommandAdapter(
            SalesChannelCategoryJpaRepository repository,
            SalesChannelCategoryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SalesChannelCategory category) {
        SalesChannelCategoryJpaEntity entity = mapper.toEntity(category);
        SalesChannelCategoryJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
