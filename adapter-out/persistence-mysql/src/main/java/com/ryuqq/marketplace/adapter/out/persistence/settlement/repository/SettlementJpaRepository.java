package com.ryuqq.marketplace.adapter.out.persistence.settlement.repository;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 정산 JPA Repository (save 용). */
public interface SettlementJpaRepository extends JpaRepository<SettlementJpaEntity, String> {}
