package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 주문 이력 JPA Repository — 이력 INSERT용. */
public interface LegacyOrderHistoryJpaRepository
        extends JpaRepository<LegacyOrderHistoryEntity, Long> {}
