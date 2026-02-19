package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.mapper.ExternalCategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.repository.ExternalCategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.application.externalcategorymapping.port.out.query.ExternalCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Query Adapter. */
@Component
public class ExternalCategoryMappingQueryAdapter implements ExternalCategoryMappingQueryPort {

    private final ExternalCategoryMappingQueryDslRepository repository;
    private final ExternalCategoryMappingJpaEntityMapper mapper;

    public ExternalCategoryMappingQueryAdapter(
            ExternalCategoryMappingQueryDslRepository repository,
            ExternalCategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ExternalCategoryMapping> findById(ExternalCategoryMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<ExternalCategoryMapping> findByExternalSourceIdAndExternalCategoryCode(
            Long externalSourceId, String externalCategoryCode) {
        return repository
                .findByExternalSourceIdAndExternalCategoryCode(
                        externalSourceId, externalCategoryCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<ExternalCategoryMapping> findByExternalSourceIdAndExternalCategoryCodes(
            Long externalSourceId, List<String> externalCategoryCodes) {
        return repository
                .findByExternalSourceIdAndExternalCategoryCodes(
                        externalSourceId, externalCategoryCodes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExternalCategoryMapping> findByExternalSourceId(Long externalSourceId) {
        return repository.findByExternalSourceId(externalSourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ExternalCategoryMapping> findByCriteria(
            ExternalCategoryMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ExternalCategoryMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
