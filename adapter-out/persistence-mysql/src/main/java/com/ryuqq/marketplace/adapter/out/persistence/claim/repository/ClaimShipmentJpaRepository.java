package com.ryuqq.marketplace.adapter.out.persistence.claim.repository;

import com.ryuqq.marketplace.adapter.out.persistence.claim.entity.ClaimShipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 클레임 배송 JPA Repository (save 용). */
public interface ClaimShipmentJpaRepository extends JpaRepository<ClaimShipmentJpaEntity, String> {}
