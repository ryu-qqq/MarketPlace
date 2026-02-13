package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionJpaRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.ProductGroupDescriptionCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionCommandAdapter - 상품 그룹 상세설명 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductGroupDescriptionCommandAdapter implements ProductGroupDescriptionCommandPort {

    private final ProductGroupDescriptionJpaRepository repository;
    private final ProductGroupDescriptionJpaEntityMapper mapper;

    public ProductGroupDescriptionCommandAdapter(
            ProductGroupDescriptionJpaRepository repository,
            ProductGroupDescriptionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroupDescription description) {
        ProductGroupDescriptionJpaEntity entity = mapper.toEntity(description);
        ProductGroupDescriptionJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
