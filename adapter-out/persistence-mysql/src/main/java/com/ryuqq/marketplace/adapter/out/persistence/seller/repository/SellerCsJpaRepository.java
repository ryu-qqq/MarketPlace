package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerCsJpaRepository - 셀러 CS 정보 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerCsJpaRepository extends JpaRepository<SellerCsJpaEntity, Long> {}
