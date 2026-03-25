package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentBillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 결제 청구서 JPA Repository. */
public interface LegacyPaymentBillJpaRepository
        extends JpaRepository<LegacyPaymentBillEntity, Long> {}
