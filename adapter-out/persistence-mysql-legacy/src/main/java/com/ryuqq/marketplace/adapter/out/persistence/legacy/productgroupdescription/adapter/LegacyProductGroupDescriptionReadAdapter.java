package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.application.legacy.description.port.out.query.LegacyProductGroupDescriptionReadPort;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상세설명 + 이미지 Read Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 JpaRepository 사용.
 */
@Component
public class LegacyProductGroupDescriptionReadAdapter
        implements LegacyProductGroupDescriptionReadPort {

    private final LegacyProductGroupDetailDescriptionJpaRepository descriptionRepository;
    private final LegacyDescriptionImageJpaRepository imageRepository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductGroupDescriptionReadAdapter(
            LegacyProductGroupDetailDescriptionJpaRepository descriptionRepository,
            LegacyDescriptionImageJpaRepository imageRepository,
            LegacyProductCommandEntityMapper mapper) {
        this.descriptionRepository = descriptionRepository;
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyProductGroupDescription> findByProductGroupId(long productGroupId) {
        Optional<LegacyProductGroupDetailDescriptionEntity> descriptionEntity =
                descriptionRepository.findById(productGroupId);
        if (descriptionEntity.isEmpty()) {
            return Optional.empty();
        }
        List<LegacyDescriptionImageEntity> imageEntities =
                imageRepository.findAllByProductGroupIdAndDeletedFalse(productGroupId);
        return Optional.of(mapper.toDescriptionDomain(descriptionEntity.get(), imageEntities));
    }
}
