package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.legacy.image.port.out.command.LegacyProductImageCommandPort;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group_image INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductImageCommandAdapter implements LegacyProductImageCommandPort {

    private final LegacyProductGroupImageJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductImageCommandAdapter(
            LegacyProductGroupImageJpaRepository repository,
            LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persistAll(List<LegacyProductImage> images) {
        List<LegacyProductGroupImageEntity> entities =
                images.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
