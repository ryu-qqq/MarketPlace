package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionDetailJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyOptionDetailCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB option_detail Command Adapter.
 *
 * <p>표준 SellerOptionValue → LegacyOptionDetailEntity 변환 후 저장.
 */
@Component
public class LegacyOptionDetailCommandAdapter implements LegacyOptionDetailCommandPort {

    private final LegacyOptionDetailJpaRepository repository;

    public LegacyOptionDetailCommandAdapter(LegacyOptionDetailJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long persist(SellerOptionValue optionValue) {
        LegacyOptionDetailEntity entity =
                LegacyOptionDetailEntity.create(
                        optionValue.sellerOptionGroupIdValue(), optionValue.optionValueNameValue());
        return repository.save(entity).getId();
    }
}
