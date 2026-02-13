package com.ryuqq.marketplace.adapter.out.persistence.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.mapper.ProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductOptionMappingJpaRepository;
import com.ryuqq.marketplace.application.product.port.out.command.ProductOptionMappingCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductOptionMappingCommandAdapter - 상품 옵션 매핑 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductOptionMappingCommandAdapter implements ProductOptionMappingCommandPort {

    private final ProductOptionMappingJpaRepository repository;
    private final ProductJpaEntityMapper mapper;

    public ProductOptionMappingCommandAdapter(
            ProductOptionMappingJpaRepository repository, ProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void deleteByProductId(Long productId) {
        repository.deleteByProductId(productId);
    }

    @Override
    public void deleteByProductIdIn(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return;
        }
        repository.deleteByProductIdIn(productIds);
    }

    @Override
    public void persistAll(Long productId, List<ProductOptionMapping> mappings) {
        if (mappings.isEmpty()) {
            return;
        }
        List<ProductOptionMappingJpaEntity> entities =
                mappings.stream().map(m -> mapper.toMappingEntity(m, productId)).toList();
        repository.saveAll(entities);
    }
}
