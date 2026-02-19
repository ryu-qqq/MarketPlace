package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.mapper.ProductGroupImageCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.repository.ProductGroupImageCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageCompositeQueryPort;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImageCompositeQueryAdapter - 이미지 Composite 조회 Adapter.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회 구현.
 *
 * <p>PER-ADP-001: Adapter는 @Component로 등록.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현.
 */
@Component
public class ProductGroupImageCompositeQueryAdapter implements ProductGroupImageCompositeQueryPort {

    private final ProductGroupImageCompositeQueryDslRepository compositeRepository;
    private final ProductGroupImageCompositeMapper compositeMapper;

    public ProductGroupImageCompositeQueryAdapter(
            ProductGroupImageCompositeQueryDslRepository compositeRepository,
            ProductGroupImageCompositeMapper compositeMapper) {
        this.compositeRepository = compositeRepository;
        this.compositeMapper = compositeMapper;
    }

    @Override
    public ProductGroupImageUploadStatusResult findImageUploadStatus(Long productGroupId) {
        return compositeMapper.toResult(compositeRepository.findByProductGroupId(productGroupId));
    }
}
