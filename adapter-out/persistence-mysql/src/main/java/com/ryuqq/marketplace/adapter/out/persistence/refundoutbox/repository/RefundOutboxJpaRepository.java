package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 환불 아웃박스 JPA Repository. */
public interface RefundOutboxJpaRepository extends JpaRepository<RefundOutboxJpaEntity, Long> {}
