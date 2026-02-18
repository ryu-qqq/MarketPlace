package com.ryuqq.marketplace.adapter.out.persistence.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.mapper.ProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductJpaRepository;
import com.ryuqq.marketplace.application.product.port.out.command.ProductCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import org.springframework.stereotype.Component;

/**
 * Product Command Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductCommandAdapter implements ProductCommandPort {

    private final ProductJpaRepository repository;
    private final ProductJpaEntityMapper mapper;

    public ProductCommandAdapter(ProductJpaRepository repository, ProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
