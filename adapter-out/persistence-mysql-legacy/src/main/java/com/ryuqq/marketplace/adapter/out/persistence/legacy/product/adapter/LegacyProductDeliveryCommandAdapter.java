package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductDeliveryJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductDeliveryCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_delivery INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductDeliveryCommandAdapter implements LegacyProductDeliveryCommandPort {

    private final LegacyProductDeliveryJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductDeliveryCommandAdapter(
            LegacyProductDeliveryJpaRepository repository,
            LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(LegacyProductGroupId productGroupId, LegacyProductDelivery delivery) {
        repository.save(mapper.toEntity(productGroupId, delivery));
    }
}
