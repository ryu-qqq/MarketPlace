package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 교환 아웃박스 JPA Repository. */
public interface ExchangeOutboxJpaRepository extends JpaRepository<ExchangeOutboxJpaEntity, Long> {}
