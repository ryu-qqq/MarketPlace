package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository.ProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.productgroupimage.port.out.command.ProductGroupImageCommandPort;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImageCommandAdapter - 상품 그룹 이미지 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductGroupImageCommandAdapter implements ProductGroupImageCommandPort {

    private final ProductGroupImageJpaRepository repository;
    private final ProductGroupJpaEntityMapper mapper;

    public ProductGroupImageCommandAdapter(
            ProductGroupImageJpaRepository repository, ProductGroupJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroupImage image) {
        ProductGroupImageJpaEntity entity = mapper.toImageEntity(image);
        return repository.save(entity).getId();
    }
}
