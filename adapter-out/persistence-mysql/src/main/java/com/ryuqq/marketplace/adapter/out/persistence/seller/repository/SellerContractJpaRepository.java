package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerContractJpaRepository - 셀러 계약 정보 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerContractJpaRepository extends JpaRepository<SellerContractJpaEntity, Long> {}
