package com.ryuqq.marketplace.adapter.out.persistence.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Product JPA Repository (save 용). PER-REP-001: MAIN 리포지토리는 save/saveAll만 사용. */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {}
