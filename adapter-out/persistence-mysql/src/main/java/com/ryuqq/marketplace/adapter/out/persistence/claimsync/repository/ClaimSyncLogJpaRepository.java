package com.ryuqq.marketplace.adapter.out.persistence.claimsync.repository;

import com.ryuqq.marketplace.adapter.out.persistence.claimsync.entity.ClaimSyncLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 클레임 동기화 로그 JPA Repository. */
public interface ClaimSyncLogJpaRepository
        extends JpaRepository<ClaimSyncLogJpaEntity, Long> {}
