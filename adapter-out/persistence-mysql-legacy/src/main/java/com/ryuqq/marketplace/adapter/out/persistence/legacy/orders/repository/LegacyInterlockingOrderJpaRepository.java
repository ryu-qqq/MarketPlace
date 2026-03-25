package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyInterlockingOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 연동 주문 JPA Repository. */
public interface LegacyInterlockingOrderJpaRepository
        extends JpaRepository<LegacyInterlockingOrderEntity, Long> {}
