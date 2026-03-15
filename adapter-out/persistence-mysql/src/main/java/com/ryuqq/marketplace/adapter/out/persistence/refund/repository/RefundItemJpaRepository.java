package com.ryuqq.marketplace.adapter.out.persistence.refund.repository;

import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 환불 대상 상품 JPA Repository. */
public interface RefundItemJpaRepository extends JpaRepository<RefundItemJpaEntity, Long> {}
