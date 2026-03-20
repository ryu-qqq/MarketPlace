package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.mapper.LegacySellerCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.repository.LegacySellerCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacyseller.port.out.LegacySellerCompositionQueryPort;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 Composition 조회 Adapter.
 *
 * <p>{@link LegacySellerCompositionQueryPort}의 구현체. luxurydb seller 테이블에서 조회 → 표준 {@link
 * SellerAdminCompositeResult}로 변환.
 */
@Component
public class LegacySellerCompositionQueryAdapter implements LegacySellerCompositionQueryPort {

    private final LegacySellerCompositeQueryDslRepository queryDslRepository;
    private final LegacySellerCompositeMapper mapper;

    public LegacySellerCompositionQueryAdapter(
            LegacySellerCompositeQueryDslRepository queryDslRepository,
            LegacySellerCompositeMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SellerAdminCompositeResult> findAdminCompositeById(long sellerId) {
        return queryDslRepository.findById(sellerId).map(mapper::toResult);
    }
}
