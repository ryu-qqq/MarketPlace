package com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.QImageUploadOutboxJpaEntity.imageUploadOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ImageUploadOutboxQueryDslRepository - 이미지 업로드 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ImageUploadOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ImageUploadOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageUploadOutboxJpaEntity> findPendingOutboxesForRetry(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.status.eq(
                                ImageUploadOutboxJpaEntity.Status.PENDING),
                        imageUploadOutboxJpaEntity.retryCount.lt(
                                imageUploadOutboxJpaEntity.maxRetry),
                        imageUploadOutboxJpaEntity.createdAt.lt(beforeTime))
                .orderBy(imageUploadOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회 (스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageUploadOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.status.eq(
                                ImageUploadOutboxJpaEntity.Status.PROCESSING),
                        imageUploadOutboxJpaEntity.updatedAt.lt(timeoutThreshold))
                .orderBy(imageUploadOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
