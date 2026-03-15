package com.ryuqq.marketplace.adapter.out.persistence.exchange.repository;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 교환 클레임 JPA Repository. */
public interface ExchangeClaimJpaRepository extends JpaRepository<ExchangeClaimJpaEntity, String> {}
