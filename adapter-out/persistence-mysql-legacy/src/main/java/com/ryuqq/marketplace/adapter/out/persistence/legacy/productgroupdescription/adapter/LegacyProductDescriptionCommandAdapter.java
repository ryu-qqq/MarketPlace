package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyProductDescriptionCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group_detail_description Command Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductDescriptionCommandAdapter implements LegacyProductDescriptionCommandPort {

    private final LegacyProductGroupDetailDescriptionJpaRepository repository;
    private final LegacyProductGroupDescriptionEntityMapper mapper;

    public LegacyProductDescriptionCommandAdapter(
            LegacyProductGroupDetailDescriptionJpaRepository repository,
            LegacyProductGroupDescriptionEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroupDescription description) {
        return repository.save(mapper.toEntity(description)).getProductGroupId();
    }
}
