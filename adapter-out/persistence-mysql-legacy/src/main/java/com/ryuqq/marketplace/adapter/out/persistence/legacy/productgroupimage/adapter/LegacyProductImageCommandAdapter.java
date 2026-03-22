package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.mapper.LegacyProductGroupImageEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command.LegacyProductImageCommandPort;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 이미지 저장 Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductImageCommandAdapter implements LegacyProductImageCommandPort {

    private final LegacyProductGroupImageJpaRepository repository;
    private final LegacyProductGroupImageEntityMapper mapper;

    public LegacyProductImageCommandAdapter(
            LegacyProductGroupImageJpaRepository repository,
            LegacyProductGroupImageEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroupImage image) {
        LegacyProductGroupImageEntity entity = mapper.toEntity(image);
        return repository.save(entity).getId();
    }
}
