package com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.LegacyProductNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyProductNoticeJpaRepository
        extends JpaRepository<LegacyProductNoticeEntity, Long> {}
