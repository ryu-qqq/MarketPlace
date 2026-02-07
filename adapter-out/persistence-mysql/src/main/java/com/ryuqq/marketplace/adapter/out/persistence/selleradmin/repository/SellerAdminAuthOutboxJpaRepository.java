package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerAdminAuthOutboxJpaRepository - 셀러 관리자 인증 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerAdminAuthOutboxJpaRepository
        extends JpaRepository<SellerAdminAuthOutboxJpaEntity, Long> {}
