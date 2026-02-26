package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductStockJpaRepository
        extends JpaRepository<LegacyProductStockEntity, Long> {}
