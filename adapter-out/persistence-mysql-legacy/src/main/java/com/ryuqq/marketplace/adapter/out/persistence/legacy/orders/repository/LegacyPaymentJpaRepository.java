package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 결제 JPA Repository. */
public interface LegacyPaymentJpaRepository extends JpaRepository<LegacyPaymentEntity, Long> {}
