package com.ryuqq.marketplace.adapter.out.persistence.cancel.repository;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 취소 JPA Repository (save 용). */
public interface CancelJpaRepository extends JpaRepository<CancelJpaEntity, String> {}
