package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.mapper.LegacySellerAuthCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.repository.LegacySellerAuthCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacySellerAuthCompositeQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 인증 정보 복합 조회 Adapter.
 *
 * <p>{@link LegacySellerAuthCompositeQueryPort}의 구현체. Repository에서 QueryDto로 조회 → Mapper에서
 * Application DTO로 변환.
 */
@Component
public class LegacySellerAuthCompositeQueryAdapter implements LegacySellerAuthCompositeQueryPort {

    private final LegacySellerAuthCompositeQueryDslRepository queryDslRepository;
    private final LegacySellerAuthCompositeMapper mapper;

    public LegacySellerAuthCompositeQueryAdapter(
            LegacySellerAuthCompositeQueryDslRepository queryDslRepository,
            LegacySellerAuthCompositeMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacySellerAuthResult> findByEmail(String email) {
        return queryDslRepository.findByEmail(email).map(mapper::toResult);
    }
}
