package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerAuthOutboxJpaRepository - 셀러 인증 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerAuthOutboxJpaRepository
        extends JpaRepository<SellerAuthOutboxJpaEntity, Long> {}
