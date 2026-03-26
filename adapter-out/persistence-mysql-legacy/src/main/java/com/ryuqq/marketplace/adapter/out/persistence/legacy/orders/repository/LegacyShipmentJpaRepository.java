package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 배송 JPA Repository. */
public interface LegacyShipmentJpaRepository extends JpaRepository<LegacyShipmentEntity, Long> {}
