package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageJpaRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.DescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * DescriptionImageCommandAdapter - 상세설명 이미지 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class DescriptionImageCommandAdapter implements DescriptionImageCommandPort {

    private final DescriptionImageJpaRepository repository;
    private final ProductGroupDescriptionJpaEntityMapper mapper;

    public DescriptionImageCommandAdapter(
            DescriptionImageJpaRepository repository,
            ProductGroupDescriptionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void deleteByDescriptionId(Long descriptionId) {
        repository.deleteByProductGroupDescriptionId(descriptionId);
    }

    @Override
    public List<Long> persistAll(Long descriptionId, List<DescriptionImage> images) {
        if (images.isEmpty()) {
            return List.of();
        }
        List<DescriptionImageJpaEntity> entities =
                images.stream().map(image -> mapper.toImageEntity(image, descriptionId)).toList();
        List<DescriptionImageJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(DescriptionImageJpaEntity::getId).toList();
    }
}
