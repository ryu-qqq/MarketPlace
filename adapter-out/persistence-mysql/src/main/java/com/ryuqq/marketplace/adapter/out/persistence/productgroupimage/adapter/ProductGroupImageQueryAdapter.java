package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository.ProductGroupImageQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImage Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductGroupImageQueryAdapter implements ProductGroupImageQueryPort {

    private final ProductGroupImageQueryDslRepository queryDslRepository;
    private final ProductGroupJpaEntityMapper mapper;

    public ProductGroupImageQueryAdapter(
            ProductGroupImageQueryDslRepository queryDslRepository,
            ProductGroupJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductGroupImage> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toImageDomain);
    }

    @Override
    public List<ProductGroupImage> findByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository.findByProductGroupId(productGroupId.value()).stream()
                .map(mapper::toImageDomain)
                .toList();
    }
}
