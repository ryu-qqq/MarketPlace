package com.ryuqq.marketplace.adapter.out.persistence.shop.repository;

import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Shop JPA Repository (save 용). */
public interface ShopJpaRepository extends JpaRepository<ShopJpaEntity, Long> {}
