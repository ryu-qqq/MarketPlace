package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.productgroup.port.out.command.ProductGroupImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import java.util.List;
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
    public void deleteByProductGroupId(Long productGroupId) {
        repository.deleteByProductGroupId(productGroupId);
    }

    @Override
    public List<Long> persistAll(Long productGroupId, List<ProductGroupImage> images) {
        if (images.isEmpty()) {
            return List.of();
        }
        List<ProductGroupImageJpaEntity> entities =
                images.stream().map(mapper::toImageEntity).toList();
        List<ProductGroupImageJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(ProductGroupImageJpaEntity::getId).toList();
    }
}
