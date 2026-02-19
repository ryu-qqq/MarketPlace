package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.mapper.ExternalBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.repository.ExternalBrandMappingQueryDslRepository;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.port.out.query.ExternalBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Query Adapter. */
@Component
public class ExternalBrandMappingQueryAdapter implements ExternalBrandMappingQueryPort {

    private final ExternalBrandMappingQueryDslRepository repository;
    private final ExternalBrandMappingJpaEntityMapper mapper;

    public ExternalBrandMappingQueryAdapter(
            ExternalBrandMappingQueryDslRepository repository,
            ExternalBrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ExternalBrandMapping> findById(ExternalBrandMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<ExternalBrandMapping> findByExternalSourceIdAndExternalBrandCode(
            Long externalSourceId, String externalBrandCode) {
        return repository
                .findByExternalSourceIdAndExternalBrandCode(externalSourceId, externalBrandCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<ExternalBrandMapping> findByExternalSourceId(Long externalSourceId) {
        return repository.findByExternalSourceId(externalSourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExternalBrandMapping> findByCriteria(ExternalBrandMappingSearchParams params) {
        return repository.findByCriteria(params).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ExternalBrandMappingSearchParams params) {
        return repository.countByCriteria(params);
    }
}
