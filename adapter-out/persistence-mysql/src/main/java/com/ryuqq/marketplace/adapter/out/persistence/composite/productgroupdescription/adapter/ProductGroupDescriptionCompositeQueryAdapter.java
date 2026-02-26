package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.mapper.ProductGroupDescriptionCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.repository.ProductGroupDescriptionCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.DescriptionCompositeQueryPort;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionCompositeQueryAdapter - 상세설명 Composite 조회 Adapter.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회 구현.
 *
 * <p>PER-ADP-001: Adapter는 @Component로 등록.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현.
 */
@Component
public class ProductGroupDescriptionCompositeQueryAdapter implements DescriptionCompositeQueryPort {

    private final ProductGroupDescriptionCompositeQueryDslRepository compositeRepository;
    private final ProductGroupDescriptionCompositeMapper compositeMapper;

    public ProductGroupDescriptionCompositeQueryAdapter(
            ProductGroupDescriptionCompositeQueryDslRepository compositeRepository,
            ProductGroupDescriptionCompositeMapper compositeMapper) {
        this.compositeRepository = compositeRepository;
        this.compositeMapper = compositeMapper;
    }

    @Override
    public DescriptionPublishStatusResult findPublishStatus(Long productGroupId) {
        return compositeRepository
                .findByProductGroupId(productGroupId)
                .map(compositeMapper::toResult)
                .orElse(DescriptionPublishStatusResult.empty(productGroupId));
    }
}
