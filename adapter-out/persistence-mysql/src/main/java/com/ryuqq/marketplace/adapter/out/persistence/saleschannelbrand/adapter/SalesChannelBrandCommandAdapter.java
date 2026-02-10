package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper.SalesChannelBrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandJpaRepository;
import com.ryuqq.marketplace.application.saleschannelbrand.port.out.command.SalesChannelBrandCommandPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Command Adapter. */
@Component
public class SalesChannelBrandCommandAdapter implements SalesChannelBrandCommandPort {

    private final SalesChannelBrandJpaRepository repository;
    private final SalesChannelBrandJpaEntityMapper mapper;

    public SalesChannelBrandCommandAdapter(
            SalesChannelBrandJpaRepository repository, SalesChannelBrandJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SalesChannelBrand brand) {
        SalesChannelBrandJpaEntity entity = mapper.toEntity(brand);
        SalesChannelBrandJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
