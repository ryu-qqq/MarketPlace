package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyConversionOutboxJpaEntity.legacyConversionOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition.LegacyConversionOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LegacyConversionOutboxQueryDslRepository - QueryDSL 기반 조회 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class LegacyConversionOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final LegacyConversionOutboxConditionBuilder conditionBuilder;

    public LegacyConversionOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, LegacyConversionOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * legacyProductGroupId로 PENDING 상태의 Outbox 조회.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return Outbox Optional
     */
    public Optional<LegacyConversionOutboxJpaEntity> findPendingByLegacyProductGroupId(
            Long legacyProductGroupId) {
        LegacyConversionOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(legacyConversionOutboxJpaEntity)
                        .where(
                                conditionBuilder.legacyProductGroupIdEq(legacyProductGroupId),
                                conditionBuilder.statusPending())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyConversionOutboxJpaEntity> findPendingOutboxes(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(legacyConversionOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(legacyConversionOutboxJpaEntity.createdAt.asc())
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
    public List<LegacyConversionOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(legacyConversionOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(legacyConversionOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
