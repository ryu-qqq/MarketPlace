package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper.InboundSourceJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository.InboundSourceQueryDslRepository;
import com.ryuqq.marketplace.application.inboundsource.port.out.query.InboundSourceQueryPort;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** InboundSource Query Adapter (InboundSourceQueryPort 구현). */
@Component
public class InboundSourceQueryAdapter implements InboundSourceQueryPort {

    private final InboundSourceQueryDslRepository repository;
    private final InboundSourceJpaEntityMapper mapper;

    public InboundSourceQueryAdapter(
            InboundSourceQueryDslRepository repository, InboundSourceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InboundSource> findById(InboundSourceId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<InboundSource> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<InboundSource> findByCriteria(InboundSourceSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(InboundSourceSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
