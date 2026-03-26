package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyDescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB legacy_description_images Command Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyDescriptionImageCommandAdapter implements LegacyDescriptionImageCommandPort {

    private final LegacyDescriptionImageJpaRepository repository;
    private final LegacyProductGroupDescriptionEntityMapper mapper;

    public LegacyDescriptionImageCommandAdapter(
            LegacyDescriptionImageJpaRepository repository,
            LegacyProductGroupDescriptionEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(DescriptionImage image) {
        return repository.save(mapper.toImageEntity(image)).getId();
    }
}
