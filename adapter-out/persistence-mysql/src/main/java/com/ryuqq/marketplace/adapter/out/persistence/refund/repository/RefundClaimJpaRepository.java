package com.ryuqq.marketplace.adapter.out.persistence.refund.repository;

import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 환불 클레임 JPA Repository. */
public interface RefundClaimJpaRepository extends JpaRepository<RefundClaimJpaEntity, String> {}
