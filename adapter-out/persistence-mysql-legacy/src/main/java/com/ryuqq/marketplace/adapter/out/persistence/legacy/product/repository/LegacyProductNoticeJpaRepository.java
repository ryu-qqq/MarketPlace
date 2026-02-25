package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductNoticeJpaRepository
        extends JpaRepository<LegacyProductNoticeEntity, Long> {}
