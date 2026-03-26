package com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.LegacyProductDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductDeliveryJpaRepository
        extends JpaRepository<LegacyProductDeliveryEntity, Long> {}
