package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductJdbcRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product Command Adapter.
 *
 * <p>표준 Product 도메인 → LegacyProductEntity (stock_quantity 포함) 변환 후 저장.
 */
@Component
public class LegacyProductCommandAdapter implements LegacyProductCommandPort {

    private final LegacyProductJpaRepository productRepository;
    private final LegacyProductJdbcRepository jdbcRepository;

    public LegacyProductCommandAdapter(
            LegacyProductJpaRepository productRepository,
            LegacyProductJdbcRepository jdbcRepository) {
        this.productRepository = productRepository;
        this.jdbcRepository = jdbcRepository;
    }

    @Override
    public Long persist(Product product) {
        LegacyProductEntity entity =
                LegacyProductEntity.create(
                        product.productGroupIdValue(), "N", "Y", product.stockQuantity());
        return productRepository.save(entity).getId();
    }

    @Override
    public void softDeleteByProductGroupId(long productGroupId) {
        jdbcRepository.softDeleteByProductGroupId(productGroupId);
    }

    @Override
    public void updateStock(long productId, int stockQuantity) {
        jdbcRepository.updateStock(productId, stockQuantity);
    }
}
