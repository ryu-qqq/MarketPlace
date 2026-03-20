package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.command.LegacyProductGroupCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group Command Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>id가 null이면 INSERT, id가 있으면 JPA merge(UPDATE).
 */
@Component
public class LegacyProductGroupCommandAdapter implements LegacyProductGroupCommandPort {

    private final LegacyProductGroupJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductGroupCommandAdapter(
            LegacyProductGroupJpaRepository repository, LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyProductGroup productGroup) {
        LegacyProductGroupEntity entity = mapper.toEntity(productGroup);
        LegacyProductGroupEntity saved = repository.save(entity);
        return saved.getId();
    }
}
