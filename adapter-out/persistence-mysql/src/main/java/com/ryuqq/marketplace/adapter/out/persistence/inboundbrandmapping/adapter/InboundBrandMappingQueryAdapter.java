package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper.InboundBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository.InboundBrandMappingQueryDslRepository;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.query.InboundBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** InboundBrandMapping Query Adapter. */
@Component
public class InboundBrandMappingQueryAdapter implements InboundBrandMappingQueryPort {

    private final InboundBrandMappingQueryDslRepository repository;
    private final InboundBrandMappingJpaEntityMapper mapper;

    public InboundBrandMappingQueryAdapter(
            InboundBrandMappingQueryDslRepository repository,
            InboundBrandMappingJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InboundBrandMapping> findById(InboundBrandMappingId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<InboundBrandMapping> findByInboundSourceIdAndExternalBrandCode(
            Long inboundSourceId, String externalBrandCode) {
        return repository
                .findByInboundSourceIdAndExternalBrandCode(inboundSourceId, externalBrandCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<InboundBrandMapping> findByInboundSourceIdAndExternalBrandCodes(
            Long inboundSourceId, List<String> externalBrandCodes) {
        return repository
                .findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, externalBrandCodes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InboundBrandMapping> findByInboundSourceId(Long inboundSourceId) {
        return repository.findByInboundSourceId(inboundSourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InboundBrandMapping> findByCriteria(InboundBrandMappingSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(InboundBrandMappingSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
