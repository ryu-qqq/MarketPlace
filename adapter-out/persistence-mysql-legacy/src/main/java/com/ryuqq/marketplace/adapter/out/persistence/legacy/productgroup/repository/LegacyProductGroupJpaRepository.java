package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductGroupJpaRepository
        extends JpaRepository<LegacyProductGroupEntity, Long> {}
