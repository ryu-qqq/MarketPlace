package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository.BrandMappingQueryDslRepository;
import com.ryuqq.marketplace.application.brandmapping.port.out.query.BrandMappingQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 브랜드 매핑 역조회 어댑터. */
@Component
public class BrandMappingQueryAdapter implements BrandMappingQueryPort {

    private final BrandMappingQueryDslRepository queryDslRepository;

    public BrandMappingQueryAdapter(BrandMappingQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return queryDslRepository.findSalesChannelBrandId(salesChannelId, internalBrandId);
    }

    @Override
    public Optional<String> findExternalBrandCode(Long salesChannelId, Long internalBrandId) {
        return queryDslRepository.findExternalBrandCode(salesChannelId, internalBrandId);
    }
}
