package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** OrderHistory JPA Repository (save 용). */
public interface OrderHistoryJpaRepository extends JpaRepository<OrderHistoryJpaEntity, Long> {}
