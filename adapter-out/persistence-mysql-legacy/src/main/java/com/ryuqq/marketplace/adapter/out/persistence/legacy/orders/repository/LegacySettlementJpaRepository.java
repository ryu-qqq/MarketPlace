package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacySettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 정산 JPA Repository. */
public interface LegacySettlementJpaRepository extends JpaRepository<LegacySettlementEntity, Long> {}
