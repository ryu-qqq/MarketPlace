package com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ImageUploadOutboxJpaRepository - 이미지 업로드 Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface ImageUploadOutboxJpaRepository
        extends JpaRepository<ImageUploadOutboxJpaEntity, Long> {}
