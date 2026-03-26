package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDescriptionQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.query.LegacyProductGroupDescriptionReadPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상세설명 + 이미지 Read Adapter.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository만 사용.
 */
@Component
public class LegacyProductGroupDescriptionReadAdapter
        implements LegacyProductGroupDescriptionReadPort {

    private final LegacyProductGroupDescriptionQueryDslRepository queryDslRepository;
    private final LegacyProductGroupDescriptionEntityMapper mapper;

    public LegacyProductGroupDescriptionReadAdapter(
            LegacyProductGroupDescriptionQueryDslRepository queryDslRepository,
            LegacyProductGroupDescriptionEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductGroupDescription> findByProductGroupId(long productGroupId) {
        Optional<LegacyProductGroupDetailDescriptionEntity> descriptionEntity =
                queryDslRepository.findDescriptionByProductGroupId(productGroupId);
        if (descriptionEntity.isEmpty()) {
            return Optional.empty();
        }
        List<LegacyDescriptionImageEntity> imageEntities =
                queryDslRepository.findImagesByProductGroupId(productGroupId);

        return Optional.of(mapper.toDomain(descriptionEntity.get(), imageEntities));
    }
}
