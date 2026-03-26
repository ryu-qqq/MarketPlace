package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** QnaOutbox JPA Repository. */
public interface QnaOutboxJpaRepository extends JpaRepository<QnaOutboxJpaEntity, Long> {}
