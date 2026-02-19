package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.mapper.ExternalBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.repository.ExternalBrandMappingQueryDslRepository;
import com.ryuqq.marketplace.application.externalbrandmapping.port.out.query.ExternalBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
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
    public List<ExternalBrandMapping> findByExternalSourceIdAndExternalBrandCodes(
            Long externalSourceId, List<String> externalBrandCodes) {
        return repository
                .findByExternalSourceIdAndExternalBrandCodes(externalSourceId, externalBrandCodes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExternalBrandMapping> findByExternalSourceId(Long externalSourceId) {
        return repository.findByExternalSourceId(externalSourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExternalBrandMapping> findByCriteria(ExternalBrandMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ExternalBrandMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
