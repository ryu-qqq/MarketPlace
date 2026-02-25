package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductStockJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductStockCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_stock INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductStockCommandAdapter implements LegacyProductStockCommandPort {

    private final LegacyProductStockJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductStockCommandAdapter(
            LegacyProductStockJpaRepository repository, LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(LegacyProductId productId, int stockQuantity) {
        repository.save(mapper.toEntity(productId, stockQuantity));
    }
}
