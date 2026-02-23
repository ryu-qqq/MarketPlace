package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.mapper.InboundCategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository.InboundCategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.query.InboundCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping Query Adapter. */
@Component
public class InboundCategoryMappingQueryAdapter implements InboundCategoryMappingQueryPort {

    private final InboundCategoryMappingQueryDslRepository repository;
    private final InboundCategoryMappingJpaEntityMapper mapper;

    public InboundCategoryMappingQueryAdapter(
            InboundCategoryMappingQueryDslRepository repository,
            InboundCategoryMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InboundCategoryMapping> findById(InboundCategoryMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<InboundCategoryMapping> findByInboundSourceIdAndExternalCategoryCode(
            Long inboundSourceId, String externalCategoryCode) {
        return repository
                .findByInboundSourceIdAndExternalCategoryCode(inboundSourceId, externalCategoryCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<InboundCategoryMapping> findByInboundSourceIdAndExternalCategoryCodes(
            Long inboundSourceId, List<String> externalCategoryCodes) {
        return repository
                .findByInboundSourceIdAndExternalCategoryCodes(
                        inboundSourceId, externalCategoryCodes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InboundCategoryMapping> findByInboundSourceId(Long inboundSourceId) {
        return repository.findByInboundSourceId(inboundSourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InboundCategoryMapping> findByCriteria(
            InboundCategoryMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(InboundCategoryMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
