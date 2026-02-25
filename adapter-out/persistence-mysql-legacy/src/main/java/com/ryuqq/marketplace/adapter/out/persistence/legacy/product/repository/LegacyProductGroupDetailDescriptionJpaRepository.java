package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductGroupDetailDescriptionJpaRepository
        extends JpaRepository<LegacyProductGroupDetailDescriptionEntity, Long> {}
