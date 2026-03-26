package com.ryuqq.marketplace.adapter.out.persistence.qna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Qna JPA Repository. */
public interface QnaJpaRepository extends JpaRepository<QnaJpaEntity, Long> {}
