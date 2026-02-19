package com.ryuqq.marketplace.adapter.out.persistence.externalsource.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.ExternalSourceJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.mapper.ExternalSourceJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.repository.ExternalSourceJpaRepository;
import com.ryuqq.marketplace.application.externalsource.port.out.command.ExternalSourceCommandPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import org.springframework.stereotype.Component;

/** ExternalSource Command Adapter. */
@Component
public class ExternalSourceCommandAdapter implements ExternalSourceCommandPort {

    private final ExternalSourceJpaRepository repository;
    private final ExternalSourceJpaEntityMapper mapper;

    public ExternalSourceCommandAdapter(
            ExternalSourceJpaRepository repository, ExternalSourceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ExternalSource externalSource) {
        ExternalSourceJpaEntity entity = mapper.toEntity(externalSource);
        ExternalSourceJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
