package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper.SalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.application.saleschannel.port.out.command.SalesChannelCommandPort;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import org.springframework.stereotype.Component;

/** SalesChannel Command Adapter. */
@Component
public class SalesChannelCommandAdapter implements SalesChannelCommandPort {

    private final SalesChannelJpaRepository repository;
    private final SalesChannelJpaEntityMapper mapper;

    public SalesChannelCommandAdapter(
            SalesChannelJpaRepository repository, SalesChannelJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SalesChannel salesChannel) {
        SalesChannelJpaEntity entity = mapper.toEntity(salesChannel);
        SalesChannelJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
