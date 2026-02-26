package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper.ChannelOptionMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository.ChannelOptionMappingQueryDslRepository;
import com.ryuqq.marketplace.application.channeloptionmapping.port.out.query.ChannelOptionMappingQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping Query Adapter. */
@Component
public class ChannelOptionMappingQueryAdapter implements ChannelOptionMappingQueryPort {

    private final ChannelOptionMappingQueryDslRepository queryDslRepository;
    private final ChannelOptionMappingJpaEntityMapper mapper;

    public ChannelOptionMappingQueryAdapter(
            ChannelOptionMappingQueryDslRepository queryDslRepository,
            ChannelOptionMappingJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ChannelOptionMapping> findById(ChannelOptionMappingId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<ChannelOptionMapping> findByCriteria(ChannelOptionMappingSearchCriteria criteria) {
        return queryDslRepository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ChannelOptionMappingSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }

    @Override
    public boolean existsBySalesChannelIdAndCanonicalOptionValueId(
            SalesChannelId salesChannelId, CanonicalOptionValueId canonicalOptionValueId) {
        return queryDslRepository.existsBySalesChannelIdAndCanonicalOptionValueId(
                salesChannelId.value(), canonicalOptionValueId.value());
    }
}
