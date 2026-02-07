package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerAddressJpaRepository - 셀러 주소 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerAddressJpaRepository extends JpaRepository<SellerAddressJpaEntity, Long> {}
