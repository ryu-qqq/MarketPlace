package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 QnA JPA Repository — 상태 UPDATE용. */
public interface LegacyQnaJpaRepository extends JpaRepository<LegacyQnaEntity, Long> {}
