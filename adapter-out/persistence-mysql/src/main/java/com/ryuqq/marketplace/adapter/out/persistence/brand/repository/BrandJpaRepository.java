package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Brand JPA Repository (save 용). */
public interface BrandJpaRepository extends JpaRepository<BrandJpaEntity, Long> {}
