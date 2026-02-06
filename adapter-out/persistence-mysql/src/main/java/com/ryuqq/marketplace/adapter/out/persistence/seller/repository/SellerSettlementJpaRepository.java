package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerSettlementJpaRepository - 셀러 정산 정보 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerSettlementJpaRepository
        extends JpaRepository<SellerSettlementJpaEntity, Long> {}
