package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** OrderItem JPA Repository (save 용). */
public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {}
