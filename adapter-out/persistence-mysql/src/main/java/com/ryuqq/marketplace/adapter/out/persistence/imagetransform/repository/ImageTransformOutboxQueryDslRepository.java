package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.QImageTransformOutboxJpaEntity.imageTransformOutboxJpaEntity;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ImageTransformOutboxQueryDslRepository - 이미지 변환 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ImageTransformOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ImageTransformOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티 (없으면 null)
     */
    public ImageTransformOutboxJpaEntity findById(Long outboxId) {
        return queryFactory
                .selectFrom(imageTransformOutboxJpaEntity)
                .where(imageTransformOutboxJpaEntity.id.eq(outboxId))
                .fetchOne();
    }

    /**
     * PENDING 상태의 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageTransformOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(imageTransformOutboxJpaEntity)
                .where(
                        imageTransformOutboxJpaEntity.status.eq(ImageTransformOutboxStatus.PENDING),
                        imageTransformOutboxJpaEntity.retryCount.lt(
                                imageTransformOutboxJpaEntity.maxRetry),
                        imageTransformOutboxJpaEntity.createdAt.lt(beforeTime))
                .orderBy(imageTransformOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * PROCESSING 상태의 Outbox 목록 조회 (폴링 스케줄러용).
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageTransformOutboxJpaEntity> findProcessingOutboxes(int limit) {
        return queryFactory
                .selectFrom(imageTransformOutboxJpaEntity)
                .where(
                        imageTransformOutboxJpaEntity.status.eq(
                                ImageTransformOutboxStatus.PROCESSING),
                        imageTransformOutboxJpaEntity.transformRequestId.isNotNull())
                .orderBy(imageTransformOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회 (타임아웃 복구 스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageTransformOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(imageTransformOutboxJpaEntity)
                .where(
                        imageTransformOutboxJpaEntity.status.eq(
                                ImageTransformOutboxStatus.PROCESSING),
                        imageTransformOutboxJpaEntity.updatedAt.lt(timeoutThreshold))
                .orderBy(imageTransformOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * 주어진 소스 이미지 ID 목록과 Variant 타입 목록에 대해 활성(PENDING/PROCESSING) 상태의 (sourceImageId, variantType)
     * 쌍을 조회합니다.
     *
     * @param sourceImageIds 소스 이미지 ID 목록
     * @param variantTypes Variant 타입 목록
     * @return (sourceImageId, variantType) Tuple 목록
     */
    public List<Tuple> findActiveOutboxPairs(
            List<Long> sourceImageIds, List<ImageVariantType> variantTypes) {
        return queryFactory
                .select(
                        imageTransformOutboxJpaEntity.sourceImageId,
                        imageTransformOutboxJpaEntity.variantType)
                .from(imageTransformOutboxJpaEntity)
                .where(
                        imageTransformOutboxJpaEntity.sourceImageId.in(sourceImageIds),
                        imageTransformOutboxJpaEntity.variantType.in(variantTypes),
                        imageTransformOutboxJpaEntity.status.in(
                                ImageTransformOutboxStatus.PENDING,
                                ImageTransformOutboxStatus.PROCESSING))
                .fetch();
    }
}
