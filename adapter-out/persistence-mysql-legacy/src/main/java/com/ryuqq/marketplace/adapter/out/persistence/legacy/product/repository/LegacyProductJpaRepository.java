package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductJpaRepository extends JpaRepository<LegacyProductEntity, Long> {}
