package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductGroupInspectionOutboxJpaRepository - 상품 그룹 검수 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface ProductGroupInspectionOutboxJpaRepository
        extends JpaRepository<ProductGroupInspectionOutboxJpaEntity, Long> {}
