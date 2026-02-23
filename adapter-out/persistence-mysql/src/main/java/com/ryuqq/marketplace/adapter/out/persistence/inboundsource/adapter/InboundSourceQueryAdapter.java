package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper.InboundSourceJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository.InboundSourceQueryDslRepository;
import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** InboundSource Query Adapter (ExternalSourceQueryPort 구현). */
@Component
public class InboundSourceQueryAdapter implements ExternalSourceQueryPort {

    private final InboundSourceQueryDslRepository repository;
    private final InboundSourceJpaEntityMapper mapper;

    public InboundSourceQueryAdapter(
            InboundSourceQueryDslRepository repository, InboundSourceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ExternalSource> findById(ExternalSourceId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<ExternalSource> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<ExternalSource> findByCriteria(ExternalSourceSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ExternalSourceSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
