package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 클레임 이력 JPA Repository (save 용). */
public interface ClaimHistoryJpaRepository extends JpaRepository<ClaimHistoryJpaEntity, String> {}
