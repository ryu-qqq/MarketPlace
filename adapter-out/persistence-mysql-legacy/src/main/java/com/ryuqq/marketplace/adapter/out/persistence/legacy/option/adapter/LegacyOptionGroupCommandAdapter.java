package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper.LegacyOptionCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionGroupJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB option_group INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyOptionGroupCommandAdapter implements LegacyOptionGroupCommandPort {

    private final LegacyOptionGroupJpaRepository repository;
    private final LegacyOptionCommandEntityMapper mapper;

    public LegacyOptionGroupCommandAdapter(
            LegacyOptionGroupJpaRepository repository, LegacyOptionCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyOptionGroup optionGroup) {
        LegacyOptionGroupEntity entity = mapper.toEntity(optionGroup);
        LegacyOptionGroupEntity saved = repository.save(entity);
        return saved.getId();
    }
}
