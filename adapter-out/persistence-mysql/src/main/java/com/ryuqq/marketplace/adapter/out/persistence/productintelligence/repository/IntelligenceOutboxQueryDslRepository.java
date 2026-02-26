package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.QIntelligenceOutboxJpaEntity.intelligenceOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * IntelligenceOutboxQueryDslRepository - Intelligence Pipeline Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class IntelligenceOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public IntelligenceOutboxQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * PENDING 상태의 Outbox 목록 조회 (Outbox Relay 스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<IntelligenceOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(intelligenceOutboxJpaEntity)
                .where(
                        intelligenceOutboxJpaEntity.status.eq(
                                IntelligenceOutboxJpaEntity.Status.PENDING),
                        intelligenceOutboxJpaEntity.retryCount.lt(
                                intelligenceOutboxJpaEntity.maxRetry),
                        intelligenceOutboxJpaEntity.createdAt.lt(beforeTime))
                .orderBy(intelligenceOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * 진행 중 상태에서 타임아웃된 Outbox 목록 조회 (타임아웃 복구 스케줄러용).
     *
     * <p>SENT 상태가 복구 대상입니다.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<IntelligenceOutboxJpaEntity> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(intelligenceOutboxJpaEntity)
                .where(
                        intelligenceOutboxJpaEntity.status.eq(
                                IntelligenceOutboxJpaEntity.Status.SENT),
                        intelligenceOutboxJpaEntity.updatedAt.lt(timeoutThreshold))
                .orderBy(intelligenceOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티
     */
    public Optional<IntelligenceOutboxJpaEntity> findById(Long outboxId) {
        IntelligenceOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(intelligenceOutboxJpaEntity)
                        .where(intelligenceOutboxJpaEntity.id.eq(outboxId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }
}
