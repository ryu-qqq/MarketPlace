package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductGroupDetailDescriptionJpaRepository
        extends JpaRepository<LegacyProductGroupDetailDescriptionEntity, Long> {}
