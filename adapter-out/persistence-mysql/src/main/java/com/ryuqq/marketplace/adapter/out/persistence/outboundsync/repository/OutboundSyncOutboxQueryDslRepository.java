package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition.OutboundSyncOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * OutboundSyncOutboxQueryDslRepository - 외부 상품 연동 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class OutboundSyncOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OutboundSyncOutboxConditionBuilder conditionBuilder;

    public OutboundSyncOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, OutboundSyncOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    private static final long MAX_PENDING_FETCH_SIZE = 1000L;

    /**
     * 상품그룹 ID로 PENDING 상태의 Outbox 목록 조회.
     *
     * <p>메모리 보호를 위해 최대 {@value #MAX_PENDING_FETCH_SIZE}건으로 제한합니다.
     *
     * @param productGroupId 상품그룹 ID
     * @return PENDING 상태의 Outbox 엔티티 목록
     */
    public List<OutboundSyncOutboxJpaEntity> findPendingByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(outboundSyncOutboxJpaEntity)
                .where(
                        conditionBuilder.productGroupIdEq(productGroupId),
                        conditionBuilder.statusPending())
                .limit(MAX_PENDING_FETCH_SIZE)
                .fetch();
    }

    /**
     * PENDING 상태이고 beforeTime 이전에 생성된 Outbox 목록 조회.
     *
     * @param beforeTime 생성일시 기준
     * @param batchSize 최대 조회 건수
     * @return PENDING 상태의 Outbox 엔티티 목록
     */
    public List<OutboundSyncOutboxJpaEntity> findPendingOutboxes(
            java.time.Instant beforeTime, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        long safeBatchSize = Math.min(batchSize, MAX_PENDING_FETCH_SIZE);
        return queryFactory
                .selectFrom(outboundSyncOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outboundSyncOutboxJpaEntity.createdAt.asc())
                .limit(safeBatchSize)
                .fetch();
    }

    /**
     * PROCESSING 상태에서 타임아웃된 Outbox 목록 조회.
     *
     * @param timeoutBefore updatedAt 기준 타임아웃 시각
     * @param batchSize 최대 조회 건수
     * @return 타임아웃된 PROCESSING 상태의 Outbox 엔티티 목록
     */
    public List<OutboundSyncOutboxJpaEntity> findProcessingTimeoutOutboxes(
            java.time.Instant timeoutBefore, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        long safeBatchSize = Math.min(batchSize, MAX_PENDING_FETCH_SIZE);
        return queryFactory
                .selectFrom(outboundSyncOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutBefore))
                .orderBy(outboundSyncOutboxJpaEntity.updatedAt.asc())
                .limit(safeBatchSize)
                .fetch();
    }

    /**
     * 상품그룹 ID + syncType으로 PENDING/PROCESSING 상태의 Outbox 목록 조회.
     *
     * <p>UPDATE 중복 방지를 위해 활성(PENDING 또는 PROCESSING) 상태의 Outbox를 조회합니다.
     *
     * @param productGroupId 상품그룹 ID
     * @param syncType 연동 타입 (예: "UPDATE")
     * @return 활성 상태의 Outbox 엔티티 목록
     */
    public List<OutboundSyncOutboxJpaEntity> findActiveByProductGroupIdAndSyncType(
            Long productGroupId, String syncType) {
        return queryFactory
                .selectFrom(outboundSyncOutboxJpaEntity)
                .where(
                        conditionBuilder.productGroupIdEq(productGroupId),
                        conditionBuilder.syncTypeEq(syncType),
                        conditionBuilder.statusPendingOrProcessing())
                .limit(MAX_PENDING_FETCH_SIZE)
                .fetch();
    }

    /**
     * ID로 Outbox 엔티티 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티 (없으면 null)
     */
    public OutboundSyncOutboxJpaEntity findById(Long outboxId) {
        return queryFactory
                .selectFrom(outboundSyncOutboxJpaEntity)
                .where(outboundSyncOutboxJpaEntity.id.eq(outboxId))
                .fetchOne();
    }
}
