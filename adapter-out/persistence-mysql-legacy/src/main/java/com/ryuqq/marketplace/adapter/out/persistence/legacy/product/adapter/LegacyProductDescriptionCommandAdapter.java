package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.application.legacy.description.port.out.command.LegacyProductDescriptionCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group_detail_description INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductDescriptionCommandAdapter implements LegacyProductDescriptionCommandPort {

    private final LegacyProductGroupDetailDescriptionJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductDescriptionCommandAdapter(
            LegacyProductGroupDetailDescriptionJpaRepository repository,
            LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(LegacyProductGroupId productGroupId, LegacyProductDescription description) {
        repository.save(mapper.toEntity(productGroupId, description));
    }

    @Override
    public void persistDescription(LegacyProductGroupDescription description) {
        repository.save(mapper.toDescriptionEntity(description));
    }
}
