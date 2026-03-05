package com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.QImageUploadOutboxJpaEntity.imageUploadOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                        imageUploadOutboxJpaEntity.status.eq(ImageUploadOutboxStatus.PENDING),
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
                        imageUploadOutboxJpaEntity.status.eq(ImageUploadOutboxStatus.PROCESSING),
                        imageUploadOutboxJpaEntity.updatedAt.lt(timeoutThreshold))
                .orderBy(imageUploadOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * sourceId 목록과 sourceType으로 Outbox 목록 조회.
     *
     * @param sourceIds 이미지 ID 목록
     * @param sourceType 이미지 소스 타입
     * @return Outbox 목록 (sourceId, createdAt DESC 정렬)
     */
    public List<ImageUploadOutboxJpaEntity> findBySourceIdsAndSourceType(
            List<Long> sourceIds, ImageSourceType sourceType) {
        if (sourceIds == null || sourceIds.isEmpty()) {
            return Collections.emptyList();
        }
        return queryFactory
                .selectFrom(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.sourceId.in(sourceIds),
                        imageUploadOutboxJpaEntity.sourceType.eq(sourceType))
                .orderBy(
                        imageUploadOutboxJpaEntity.sourceId.asc(),
                        imageUploadOutboxJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * PROCESSING 상태의 Outbox 목록 조회 (폴링 스케줄러용).
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageUploadOutboxJpaEntity> findProcessingOutboxes(int limit) {
        return queryFactory
                .selectFrom(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.status.eq(ImageUploadOutboxStatus.PROCESSING),
                        imageUploadOutboxJpaEntity.downloadTaskId.isNotNull())
                .orderBy(imageUploadOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * 복구 가능한 FAILED Outbox 목록 조회.
     *
     * @param failedBefore 이 시간 이전에 FAILED된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageUploadOutboxJpaEntity> findRecoverableFailedOutboxes(
            Instant failedBefore, int limit) {
        return queryFactory
                .selectFrom(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.status.eq(ImageUploadOutboxStatus.FAILED),
                        imageUploadOutboxJpaEntity.processedAt.lt(failedBefore),
                        imageUploadOutboxJpaEntity.errorMessage.notLike("%잘못된 요청%"))
                .orderBy(imageUploadOutboxJpaEntity.processedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * downloadTaskId로 PROCESSING 상태의 Outbox 조회 (콜백용).
     *
     * @param downloadTaskId FileFlow 다운로드 태스크 ID
     * @return Outbox (Optional)
     */
    public Optional<ImageUploadOutboxJpaEntity> findProcessingByDownloadTaskId(
            String downloadTaskId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(imageUploadOutboxJpaEntity)
                        .where(
                                imageUploadOutboxJpaEntity.downloadTaskId.eq(downloadTaskId),
                                imageUploadOutboxJpaEntity.status.eq(
                                        ImageUploadOutboxStatus.PROCESSING))
                        .fetchOne());
    }
}
