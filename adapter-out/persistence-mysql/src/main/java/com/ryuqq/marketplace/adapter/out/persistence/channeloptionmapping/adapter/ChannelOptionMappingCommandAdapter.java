package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper.ChannelOptionMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository.ChannelOptionMappingJpaRepository;
import com.ryuqq.marketplace.application.channeloptionmapping.port.out.command.ChannelOptionMappingCommandPort;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping Command Adapter. */
@Component
public class ChannelOptionMappingCommandAdapter implements ChannelOptionMappingCommandPort {

    private final ChannelOptionMappingJpaRepository repository;
    private final ChannelOptionMappingJpaEntityMapper mapper;

    public ChannelOptionMappingCommandAdapter(
            ChannelOptionMappingJpaRepository repository,
            ChannelOptionMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ChannelOptionMapping channelOptionMapping) {
        ChannelOptionMappingJpaEntity entity = mapper.toEntity(channelOptionMapping);
        ChannelOptionMappingJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
