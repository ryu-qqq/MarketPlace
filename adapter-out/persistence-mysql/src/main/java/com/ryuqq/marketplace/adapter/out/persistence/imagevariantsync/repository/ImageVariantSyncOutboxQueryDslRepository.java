package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity.QImageVariantSyncOutboxJpaEntity.imageVariantSyncOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity.ImageVariantSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagevariantsync.vo.ImageVariantSyncOutboxStatus;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ImageVariantSyncOutboxQueryDslRepository - 이미지 Variant Sync Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ImageVariantSyncOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ImageVariantSyncOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * PENDING 상태의 Outbox 목록 조회 (스케줄러용).
     *
     * <p>createdAt 오래된 순으로 정렬하여 조회합니다.
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ImageVariantSyncOutboxJpaEntity> findPendingOutboxes(int limit) {
        return queryFactory
                .selectFrom(imageVariantSyncOutboxJpaEntity)
                .where(
                        imageVariantSyncOutboxJpaEntity.status.eq(
                                ImageVariantSyncOutboxStatus.PENDING),
                        imageVariantSyncOutboxJpaEntity.retryCount.lt(
                                imageVariantSyncOutboxJpaEntity.maxRetry))
                .orderBy(imageVariantSyncOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * 해당 sourceImageId에 PENDING 상태의 Outbox가 존재하는지 확인합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @return PENDING Outbox 존재 여부
     */
    public boolean existsPendingBySourceImageId(long sourceImageId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(imageVariantSyncOutboxJpaEntity)
                        .where(
                                imageVariantSyncOutboxJpaEntity.sourceImageId.eq(sourceImageId),
                                imageVariantSyncOutboxJpaEntity.status.eq(
                                        ImageVariantSyncOutboxStatus.PENDING))
                        .fetchFirst();
        return result != null;
    }
}
