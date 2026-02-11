package com.ryuqq.marketplace.adapter.out.persistence.notice.repository;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** NoticeCategory JPA Repository. */
public interface NoticeCategoryJpaRepository extends JpaRepository<NoticeCategoryJpaEntity, Long> {}
