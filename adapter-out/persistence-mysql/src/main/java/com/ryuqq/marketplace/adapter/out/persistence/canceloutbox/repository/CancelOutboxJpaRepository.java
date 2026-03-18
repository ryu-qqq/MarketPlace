package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 취소 아웃박스 JPA Repository. */
public interface CancelOutboxJpaRepository extends JpaRepository<CancelOutboxJpaEntity, Long> {}
