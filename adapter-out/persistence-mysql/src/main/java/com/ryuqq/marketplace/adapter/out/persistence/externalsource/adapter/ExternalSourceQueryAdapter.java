package com.ryuqq.marketplace.adapter.out.persistence.externalsource.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalsource.mapper.ExternalSourceJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.repository.ExternalSourceQueryDslRepository;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** ExternalSource Query Adapter. */
@Component
public class ExternalSourceQueryAdapter implements ExternalSourceQueryPort {

    private final ExternalSourceQueryDslRepository repository;
    private final ExternalSourceJpaEntityMapper mapper;

    public ExternalSourceQueryAdapter(
            ExternalSourceQueryDslRepository repository, ExternalSourceJpaEntityMapper mapper) {
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
    public List<ExternalSource> findByCriteria(ExternalSourceSearchParams params) {
        return repository.findByCriteria(params).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ExternalSourceSearchParams params) {
        return repository.countByCriteria(params);
    }
}
