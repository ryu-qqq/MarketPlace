package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.repository;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity.ImageVariantSyncOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ImageVariantSyncOutboxJpaRepository - 이미지 Variant Sync Outbox JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface ImageVariantSyncOutboxJpaRepository
        extends JpaRepository<ImageVariantSyncOutboxJpaEntity, Long> {}
