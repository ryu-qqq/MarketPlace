package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** OrderItemHistory JPA Repository (save 용). */
public interface OrderItemHistoryJpaRepository
        extends JpaRepository<OrderItemHistoryJpaEntity, Long> {}
