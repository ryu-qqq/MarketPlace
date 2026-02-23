package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductProfileJpaRepository - 상품 프로파일 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface ProductProfileJpaRepository extends JpaRepository<ProductProfileJpaEntity, Long> {}
