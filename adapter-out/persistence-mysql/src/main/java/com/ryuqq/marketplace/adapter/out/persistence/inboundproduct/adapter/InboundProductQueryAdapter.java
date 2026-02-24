package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper.InboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository.InboundProductJpaRepository;
import com.ryuqq.marketplace.application.inboundproduct.port.out.query.InboundProductQueryPort;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InboundProductQueryAdapter implements InboundProductQueryPort {

    private final InboundProductJpaRepository repository;
    private final InboundProductJpaEntityMapper mapper;

    public InboundProductQueryAdapter(
            InboundProductJpaRepository repository, InboundProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InboundProduct> findByInboundSourceIdAndProductCode(
            Long inboundSourceId, String externalProductCode) {
        return repository
                .findByInboundSourceIdAndExternalProductCode(inboundSourceId, externalProductCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<InboundProduct> findByStatus(InboundProductStatus status, int limit) {
        return repository.findTop100ByStatusOrderByCreatedAtAsc(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InboundProduct> findByStatusAndRetryCountLessThan(
            InboundProductStatus status, int maxRetryCount, int limit) {
        return repository
                .findTop50ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
                        status.name(), maxRetryCount)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
