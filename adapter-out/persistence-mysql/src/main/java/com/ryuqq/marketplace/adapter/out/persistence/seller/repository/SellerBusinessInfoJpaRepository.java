package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerBusinessInfoJpaRepository - 셀러 사업자 정보 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerBusinessInfoJpaRepository
        extends JpaRepository<SellerBusinessInfoJpaEntity, Long> {}
