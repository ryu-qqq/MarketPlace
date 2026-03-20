package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command.LegacyDescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyDescriptionImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB legacy_description_images Command Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyDescriptionImageCommandAdapter implements LegacyDescriptionImageCommandPort {

    private final LegacyDescriptionImageJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyDescriptionImageCommandAdapter(
            LegacyDescriptionImageJpaRepository repository,
            LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persistAll(List<LegacyDescriptionImage> images) {
        repository.saveAll(images.stream().map(mapper::toImageEntity).toList());
    }

    @Override
    public void softDeleteAll(List<LegacyDescriptionImage> images) {
        repository.saveAll(images.stream().map(mapper::toImageEntity).toList());
    }
}
