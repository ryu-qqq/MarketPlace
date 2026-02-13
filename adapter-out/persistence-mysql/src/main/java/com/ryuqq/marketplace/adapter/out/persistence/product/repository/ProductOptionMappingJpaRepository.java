package com.ryuqq.marketplace.adapter.out.persistence.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductOptionMapping JPA Repository.
 *
 * <p>PER-REP-001: Child 리포지토리는 deleteByXxx 메서드 허용.
 */
public interface ProductOptionMappingJpaRepository
        extends JpaRepository<ProductOptionMappingJpaEntity, Long> {

    void deleteByProductId(Long productId);

    void deleteByProductIdIn(List<Long> productIds);
}
