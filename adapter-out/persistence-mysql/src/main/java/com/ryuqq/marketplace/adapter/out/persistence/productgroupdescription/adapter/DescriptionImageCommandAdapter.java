package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageJpaRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.DescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
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
    public Long persist(DescriptionImage image) {
        DescriptionImageJpaEntity entity = mapper.toImageEntity(image);
        return repository.save(entity).getId();
    }
}
