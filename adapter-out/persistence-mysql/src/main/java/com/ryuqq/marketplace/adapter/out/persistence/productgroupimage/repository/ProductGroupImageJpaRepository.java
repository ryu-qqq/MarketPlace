package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ProductGroupImage JPA Repository. */
public interface ProductGroupImageJpaRepository
        extends JpaRepository<ProductGroupImageJpaEntity, Long> {

    void deleteByProductGroupId(Long productGroupId);
}
