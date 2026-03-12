package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper.OutboundProductImageJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository.OutboundProductImageJpaRepository;
import com.ryuqq.marketplace.application.outboundproductimage.port.out.query.OutboundProductImageQueryPort;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductImageQueryAdapter implements OutboundProductImageQueryPort {

    private final OutboundProductImageJpaRepository repository;
    private final OutboundProductImageJpaEntityMapper mapper;

    public OutboundProductImageQueryAdapter(
            OutboundProductImageJpaRepository repository,
            OutboundProductImageJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<OutboundProductImage> findActiveByOutboundProductId(Long outboundProductId) {
        return repository.findByOutboundProductIdAndDeletedFalse(outboundProductId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
