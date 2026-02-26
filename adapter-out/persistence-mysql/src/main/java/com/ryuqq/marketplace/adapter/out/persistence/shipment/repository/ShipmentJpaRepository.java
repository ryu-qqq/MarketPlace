package com.ryuqq.marketplace.adapter.out.persistence.shipment.repository;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Shipment JPA Repository (save 용). */
public interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, String> {}
