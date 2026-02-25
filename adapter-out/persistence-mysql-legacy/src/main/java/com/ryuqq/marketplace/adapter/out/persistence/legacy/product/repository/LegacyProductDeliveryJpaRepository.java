package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductDeliveryJpaRepository
        extends JpaRepository<LegacyProductDeliveryEntity, Long> {}
