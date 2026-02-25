package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductStockJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductStockCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_stock INSERT/UPDATE Adapter.
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

    @Override
    public void persistAll(List<LegacyProduct> products) {
        List<LegacyProductStockEntity> entities =
                products.stream().map(mapper::toStockEntity).toList();
        repository.saveAll(entities);
    }
}
