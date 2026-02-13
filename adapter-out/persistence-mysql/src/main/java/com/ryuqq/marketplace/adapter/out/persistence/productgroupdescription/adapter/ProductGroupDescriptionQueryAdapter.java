package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.ProductGroupDescriptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionQueryAdapter - 상품 그룹 상세설명 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 */
@Component
public class ProductGroupDescriptionQueryAdapter implements ProductGroupDescriptionQueryPort {

    private final ProductGroupDescriptionQueryDslRepository queryDslRepository;
    private final ProductGroupDescriptionJpaEntityMapper mapper;

    public ProductGroupDescriptionQueryAdapter(
            ProductGroupDescriptionQueryDslRepository queryDslRepository,
            ProductGroupDescriptionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository
                .findByProductGroupId(productGroupId.value())
                .map(
                        entity -> {
                            var imageEntities =
                                    queryDslRepository.findImagesByDescriptionId(entity.getId());
                            return mapper.toDomain(entity, imageEntities);
                        });
    }
}
