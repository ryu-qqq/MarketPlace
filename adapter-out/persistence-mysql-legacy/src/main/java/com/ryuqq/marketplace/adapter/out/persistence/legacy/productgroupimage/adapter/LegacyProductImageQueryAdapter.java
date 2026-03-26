package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.mapper.LegacyProductGroupImageEntityMapper;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.query.LegacyProductImageQueryPort;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
    private final LegacyProductGroupImageEntityMapper mapper;

    public LegacyProductImageQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository,
            LegacyProductGroupImageEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductGroupImage> findByProductGroupId(long productGroupId) {
        return queryDslRepository.findImagesByProductGroupId(productGroupId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
