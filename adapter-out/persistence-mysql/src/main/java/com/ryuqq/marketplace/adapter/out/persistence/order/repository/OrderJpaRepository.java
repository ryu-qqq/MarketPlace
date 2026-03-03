package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Order JPA Repository (save 용). */
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {}
