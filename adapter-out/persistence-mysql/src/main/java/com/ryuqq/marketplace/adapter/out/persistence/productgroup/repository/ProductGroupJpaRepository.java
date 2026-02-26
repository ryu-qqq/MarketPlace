package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ProductGroup JPA Repository (save 용). */
public interface ProductGroupJpaRepository extends JpaRepository<ProductGroupJpaEntity, Long> {}
