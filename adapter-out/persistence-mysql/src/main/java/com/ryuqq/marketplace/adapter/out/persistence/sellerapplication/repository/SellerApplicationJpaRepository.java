package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerApplicationJpaRepository - 셀러 입점 신청 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerApplicationJpaRepository
        extends JpaRepository<SellerApplicationJpaEntity, Long> {}
