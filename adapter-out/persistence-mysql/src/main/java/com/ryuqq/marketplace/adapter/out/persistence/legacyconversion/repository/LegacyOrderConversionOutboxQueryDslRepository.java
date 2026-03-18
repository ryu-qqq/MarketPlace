package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyOrderConversionOutboxJpaEntity.legacyOrderConversionOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition.LegacyOrderConversionOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * LegacyOrderConversionOutboxQueryDslRepository - QueryDSL 기반 조회 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class LegacyOrderConversionOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final LegacyOrderConversionOutboxConditionBuilder conditionBuilder;

    public LegacyOrderConversionOutboxQueryDslRepository(
            JPAQueryFactory queryFactory,
            LegacyOrderConversionOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<LegacyOrderConversionOutboxJpaEntity> findPendingOutboxes(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(legacyOrderConversionOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(legacyOrderConversionOutboxJpaEntity.createdAt.asc())
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
    public List<LegacyOrderConversionOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(legacyOrderConversionOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(legacyOrderConversionOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
