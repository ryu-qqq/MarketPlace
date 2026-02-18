package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.QProductGroupInspectionOutboxJpaEntity.productGroupInspectionOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductGroupInspectionOutboxQueryDslRepository - 상품 그룹 검수 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ProductGroupInspectionOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ProductGroupInspectionOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * PENDING 상태의 Outbox 목록 조회 (Outbox Relay 스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ProductGroupInspectionOutboxJpaEntity> findPendingOutboxes(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(productGroupInspectionOutboxJpaEntity)
                .where(
                        productGroupInspectionOutboxJpaEntity.status.eq(
                                ProductGroupInspectionOutboxJpaEntity.Status.PENDING),
                        productGroupInspectionOutboxJpaEntity.retryCount.lt(
                                productGroupInspectionOutboxJpaEntity.maxRetry),
                        productGroupInspectionOutboxJpaEntity.createdAt.lt(beforeTime))
                .orderBy(productGroupInspectionOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * 진행 중 상태에서 타임아웃된 Outbox 목록 조회 (타임아웃 복구 스케줄러용).
     *
     * <p>SENT, SCORING, ENHANCING, VERIFYING 상태 모두 복구 대상.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<ProductGroupInspectionOutboxJpaEntity> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(productGroupInspectionOutboxJpaEntity)
                .where(
                        productGroupInspectionOutboxJpaEntity.status.in(
                                ProductGroupInspectionOutboxJpaEntity.Status.SENT,
                                ProductGroupInspectionOutboxJpaEntity.Status.SCORING,
                                ProductGroupInspectionOutboxJpaEntity.Status.ENHANCING,
                                ProductGroupInspectionOutboxJpaEntity.Status.VERIFYING),
                        productGroupInspectionOutboxJpaEntity.updatedAt.lt(timeoutThreshold))
                .orderBy(productGroupInspectionOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티
     */
    public Optional<ProductGroupInspectionOutboxJpaEntity> findById(Long outboxId) {
        ProductGroupInspectionOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(productGroupInspectionOutboxJpaEntity)
                        .where(productGroupInspectionOutboxJpaEntity.id.eq(outboxId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }
}
