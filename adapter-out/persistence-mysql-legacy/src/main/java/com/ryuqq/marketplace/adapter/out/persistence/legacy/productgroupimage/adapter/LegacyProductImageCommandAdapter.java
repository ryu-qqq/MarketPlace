package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command.LegacyProductImageCommandPort;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 이미지 저장 Adapter.
 *
 * <p>표준 커맨드의 images를 luxurydb product_group_image 테이블에 저장합니다.
 * 기존 이미지 soft delete 후 새 이미지를 insert하는 replace-all 패턴.
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

    @Override
    public void update(UpdateProductGroupImagesCommand command) {
        repository.softDeleteAllByProductGroupId(command.productGroupId());

        List<LegacyProductGroupImageEntity> entities =
                command.images().stream()
                        .map(
                                img ->
                                        LegacyProductGroupImageEntity.create(
                                                null,
                                                command.productGroupId(),
                                                img.imageType(),
                                                img.originUrl(),
                                                img.originUrl(),
                                                img.sortOrder(),
                                                "N"))
                        .toList();

        repository.saveAll(entities);
    }
}
