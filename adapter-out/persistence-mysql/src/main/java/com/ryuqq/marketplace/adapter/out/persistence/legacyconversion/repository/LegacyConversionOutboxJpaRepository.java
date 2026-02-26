package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LegacyConversionOutboxJpaRepository - 레거시 변환 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface LegacyConversionOutboxJpaRepository
        extends JpaRepository<LegacyConversionOutboxJpaEntity, Long> {}
