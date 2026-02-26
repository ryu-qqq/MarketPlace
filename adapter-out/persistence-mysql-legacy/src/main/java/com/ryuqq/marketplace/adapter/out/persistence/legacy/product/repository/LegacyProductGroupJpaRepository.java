package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductGroupJpaRepository
        extends JpaRepository<LegacyProductGroupEntity, Long> {}
