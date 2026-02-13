package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupJpaRepository;
import com.ryuqq.marketplace.application.productgroup.port.out.command.ProductGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import org.springframework.stereotype.Component;

/**
 * ProductGroupCommandAdapter - 상품 그룹 Command 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductGroupCommandAdapter implements ProductGroupCommandPort {

    private final ProductGroupJpaRepository repository;
    private final ProductGroupJpaEntityMapper mapper;

    public ProductGroupCommandAdapter(
            ProductGroupJpaRepository repository, ProductGroupJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroup productGroup) {
        ProductGroupJpaEntity entity = mapper.toEntity(productGroup);
        ProductGroupJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
