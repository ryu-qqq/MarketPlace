package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper.OutboundProductImageJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository.OutboundProductImageQueryDslRepository;
import com.ryuqq.marketplace.application.outboundproductimage.port.out.query.OutboundProductImageQueryPort;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductImageQueryAdapter implements OutboundProductImageQueryPort {

    private final OutboundProductImageQueryDslRepository queryDslRepository;
    private final OutboundProductImageJpaEntityMapper mapper;

    public OutboundProductImageQueryAdapter(
            OutboundProductImageQueryDslRepository queryDslRepository,
            OutboundProductImageJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OutboundProductImage> findActiveByOutboundProductId(Long outboundProductId) {
        return queryDslRepository.findActiveByOutboundProductId(outboundProductId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
