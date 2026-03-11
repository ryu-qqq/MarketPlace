package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 배송 아웃박스 JPA Repository. */
public interface ShipmentOutboxJpaRepository extends JpaRepository<ShipmentOutboxJpaEntity, Long> {}
