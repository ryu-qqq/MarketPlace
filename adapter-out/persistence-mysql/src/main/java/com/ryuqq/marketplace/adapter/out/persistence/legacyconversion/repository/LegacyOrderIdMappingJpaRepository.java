package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LegacyOrderIdMappingJpaRepository - 레거시 주문 ID 매핑 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface LegacyOrderIdMappingJpaRepository
        extends JpaRepository<LegacyOrderIdMappingJpaEntity, Long> {}
