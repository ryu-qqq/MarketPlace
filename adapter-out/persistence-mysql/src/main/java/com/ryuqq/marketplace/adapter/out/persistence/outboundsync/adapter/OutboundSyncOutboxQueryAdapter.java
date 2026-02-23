package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;

/** 외부 상품 연동 Outbox 조회 어댑터. */
@Component
public class OutboundSyncOutboxQueryAdapter implements OutboundSyncOutboxQueryPort {

    private final OutboundSyncOutboxQueryDslRepository queryDslRepository;
    private final OutboundSyncOutboxJpaEntityMapper mapper;

    public OutboundSyncOutboxQueryAdapter(
            OutboundSyncOutboxQueryDslRepository queryDslRepository,
            OutboundSyncOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OutboundSyncOutbox> findPendingByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository.findPendingByProductGroupId(productGroupId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
