package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Intelligence Pipeline Outbox JPA Repository (save 전용). */
public interface IntelligenceOutboxJpaRepository
        extends JpaRepository<IntelligenceOutboxJpaEntity, Long> {}
