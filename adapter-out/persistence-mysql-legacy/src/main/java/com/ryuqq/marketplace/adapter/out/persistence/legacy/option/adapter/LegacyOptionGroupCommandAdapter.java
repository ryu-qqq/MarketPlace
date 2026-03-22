package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionGroupJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB option_group Command Adapter.
 *
 * <p>표준 SellerOptionGroup → LegacyOptionGroupEntity (productGroupId 포함) 변환 후 저장.
 */
@Component
public class LegacyOptionGroupCommandAdapter implements LegacyOptionGroupCommandPort {

    private final LegacyOptionGroupJpaRepository repository;

    public LegacyOptionGroupCommandAdapter(LegacyOptionGroupJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long persist(SellerOptionGroup optionGroup) {
        LegacyOptionGroupEntity entity =
                LegacyOptionGroupEntity.create(
                        optionGroup.productGroupIdValue(), optionGroup.optionGroupNameValue());
        return repository.save(entity).getId();
    }
}
