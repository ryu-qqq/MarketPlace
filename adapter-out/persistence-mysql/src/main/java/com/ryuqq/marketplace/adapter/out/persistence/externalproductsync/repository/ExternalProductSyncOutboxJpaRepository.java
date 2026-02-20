package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.repository;

import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.ExternalProductSyncOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ExternalProductSyncOutboxJpaRepository - 외부 상품 연동 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface ExternalProductSyncOutboxJpaRepository
        extends JpaRepository<ExternalProductSyncOutboxJpaEntity, Long> {}
