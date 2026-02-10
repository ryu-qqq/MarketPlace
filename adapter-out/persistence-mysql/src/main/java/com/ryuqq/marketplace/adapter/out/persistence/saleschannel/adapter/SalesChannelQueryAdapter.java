package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper.SalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelQueryDslRepository;
import com.ryuqq.marketplace.application.saleschannel.port.out.query.SalesChannelQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** SalesChannel Query Adapter. */
@Component
public class SalesChannelQueryAdapter implements SalesChannelQueryPort {

    private final SalesChannelQueryDslRepository repository;
    private final SalesChannelJpaEntityMapper mapper;

    public SalesChannelQueryAdapter(
            SalesChannelQueryDslRepository repository, SalesChannelJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SalesChannel> findById(SalesChannelId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SalesChannel> findByCriteria(SalesChannelSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(SalesChannelSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsByChannelName(String channelName) {
        return repository.existsByChannelName(channelName);
    }

    @Override
    public boolean existsByChannelNameExcluding(String channelName, SalesChannelId excludeId) {
        return repository.existsByChannelNameExcluding(channelName, excludeId.value());
    }
}
