package com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.repository;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.SettlementEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 정산 원장 JPA Repository (save 용). */
public interface SettlementEntryJpaRepository
        extends JpaRepository<SettlementEntryJpaEntity, String> {}
