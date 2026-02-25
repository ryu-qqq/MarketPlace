package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.query.LegacyProductImageQueryPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group_image 조회 Adapter.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository만 사용.
 */
@Component
public class LegacyProductImageQueryAdapter implements LegacyProductImageQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductImageQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository,
            LegacyProductCommandEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyProductImage> findByProductGroupId(LegacyProductGroupId productGroupId) {
        return queryDslRepository.findImagesByProductGroupId(productGroupId.value()).stream()
                .map(mapper::toImageDomain)
                .toList();
    }
}
