package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity.ExternalOrderItemMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 외부 주문상품 매핑 JPA Repository. */
public interface ExternalOrderItemMappingJpaRepository
        extends JpaRepository<ExternalOrderItemMappingJpaEntity, Long> {}
