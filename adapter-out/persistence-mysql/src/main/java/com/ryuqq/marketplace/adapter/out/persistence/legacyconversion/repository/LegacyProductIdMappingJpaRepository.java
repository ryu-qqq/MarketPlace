package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LegacyProductIdMappingJpaRepository - JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface LegacyProductIdMappingJpaRepository
        extends JpaRepository<LegacyProductIdMappingJpaEntity, Long> {}
