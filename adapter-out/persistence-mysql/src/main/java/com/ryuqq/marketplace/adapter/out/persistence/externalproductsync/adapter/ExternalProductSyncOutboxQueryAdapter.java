package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.mapper.ExternalProductSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.repository.ExternalProductSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.externalproductsync.port.out.query.ExternalProductSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 외부 상품 연동 Outbox 조회 어댑터. */
@Component
public class ExternalProductSyncOutboxQueryAdapter implements ExternalProductSyncOutboxQueryPort {

    private final ExternalProductSyncOutboxQueryDslRepository queryDslRepository;
    private final ExternalProductSyncOutboxJpaEntityMapper mapper;

    public ExternalProductSyncOutboxQueryAdapter(
            ExternalProductSyncOutboxQueryDslRepository queryDslRepository,
            ExternalProductSyncOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ExternalProductSyncOutbox> findPendingByProductGroupId(Long productGroupId) {
        return queryDslRepository.findPendingByProductGroupId(productGroupId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
