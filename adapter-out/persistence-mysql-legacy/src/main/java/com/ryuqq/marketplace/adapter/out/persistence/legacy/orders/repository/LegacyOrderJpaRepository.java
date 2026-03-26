package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 주문 JPA Repository — 상태 UPDATE용. */
public interface LegacyOrderJpaRepository extends JpaRepository<LegacyOrderEntity, Long> {}
