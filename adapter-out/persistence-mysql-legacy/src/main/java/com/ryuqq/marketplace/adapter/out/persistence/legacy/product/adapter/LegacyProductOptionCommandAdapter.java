package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductOptionJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductOptionCommandPort;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_option Command Adapter.
 *
 * <p>표준 ProductOptionMapping → LegacyProductOptionEntity 변환 후 저장.
 */
@Component
public class LegacyProductOptionCommandAdapter implements LegacyProductOptionCommandPort {

    private final LegacyProductOptionJpaRepository repository;

    public LegacyProductOptionCommandAdapter(LegacyProductOptionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void persist(ProductOptionMapping mapping) {
        LegacyProductOptionEntity entity =
                LegacyProductOptionEntity.create(
                        mapping.productIdValue(),
                        mapping.sellerOptionValueIdValue(),
                        mapping.sellerOptionValueIdValue(),
                        0L);
        repository.save(entity);
    }
}
