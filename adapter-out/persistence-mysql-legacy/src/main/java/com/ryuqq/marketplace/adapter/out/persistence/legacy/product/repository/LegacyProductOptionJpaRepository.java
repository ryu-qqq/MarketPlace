package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductOptionJpaRepository
        extends JpaRepository<LegacyProductOptionEntity, Long> {}
