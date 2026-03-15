package com.ryuqq.marketplace.adapter.out.persistence.exchange.repository;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 교환 대상 상품 JPA Repository. */
public interface ExchangeItemJpaRepository extends JpaRepository<ExchangeItemJpaEntity, Long> {}
