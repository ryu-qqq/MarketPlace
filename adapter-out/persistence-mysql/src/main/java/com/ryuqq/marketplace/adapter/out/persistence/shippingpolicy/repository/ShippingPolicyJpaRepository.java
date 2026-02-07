package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ShippingPolicyJpaRepository - 배송 정책 JPA 레포지토리.
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ShippingPolicyJpaRepository extends JpaRepository<ShippingPolicyJpaEntity, Long> {}
