package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** SellerOptionValue JPA Repository. */
public interface SellerOptionValueJpaRepository
        extends JpaRepository<SellerOptionValueJpaEntity, Long> {}
