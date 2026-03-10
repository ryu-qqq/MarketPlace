package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Payment JPA Repository. */
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {}
