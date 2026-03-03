package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundMappingQueryDslRepository;
import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundMappingQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 아웃바운드 매핑 역조회 어댑터. */
@Component
public class OutboundMappingQueryAdapter implements OutboundMappingQueryPort {

    private final OutboundMappingQueryDslRepository queryDslRepository;

    public OutboundMappingQueryAdapter(OutboundMappingQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public Optional<Long> findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return Optional.ofNullable(
                queryDslRepository.findSalesChannelCategoryId(salesChannelId, internalCategoryId));
    }

    @Override
    public Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return Optional.ofNullable(
                queryDslRepository.findSalesChannelBrandId(salesChannelId, internalBrandId));
    }
}
