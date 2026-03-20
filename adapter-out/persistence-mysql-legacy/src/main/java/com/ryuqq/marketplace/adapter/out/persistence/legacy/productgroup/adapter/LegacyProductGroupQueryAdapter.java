package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupQueryPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group Query Adapter.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository 사용.
 */
@Component
public class LegacyProductGroupQueryAdapter implements LegacyProductGroupQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductGroupQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository,
            LegacyProductCommandEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyProductGroup> findById(LegacyProductGroupId productGroupId) {
        return queryDslRepository
                .findProductGroupById(productGroupId.value())
                .map(mapper::toDomain);
    }
}
