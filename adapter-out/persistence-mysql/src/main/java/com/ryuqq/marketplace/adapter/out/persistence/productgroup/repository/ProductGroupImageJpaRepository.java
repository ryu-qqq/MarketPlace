package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ProductGroupImage JPA Repository. */
public interface ProductGroupImageJpaRepository
        extends JpaRepository<ProductGroupImageJpaEntity, Long> {

    void deleteByProductGroupId(Long productGroupId);
}
