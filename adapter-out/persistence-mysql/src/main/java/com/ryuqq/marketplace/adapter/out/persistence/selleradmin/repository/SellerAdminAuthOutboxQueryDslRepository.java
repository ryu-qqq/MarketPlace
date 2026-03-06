package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminAuthOutboxJpaEntity.sellerAdminAuthOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminAuthOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerAdminAuthOutboxQueryDslRepository - 셀러 관리자 인증 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerAdminAuthOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerAdminAuthOutboxConditionBuilder conditionBuilder;

    public SellerAdminAuthOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, SellerAdminAuthOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티 (없으면 null)
     */
    public SellerAdminAuthOutboxJpaEntity findById(Long outboxId) {
        return queryFactory
                .selectFrom(sellerAdminAuthOutboxJpaEntity)
                .where(sellerAdminAuthOutboxJpaEntity.id.eq(outboxId))
                .fetchOne();
    }

    /**
     * SellerAdminId로 PENDING 상태의 Outbox 조회.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return Outbox Optional
     */
    public Optional<SellerAdminAuthOutboxJpaEntity> findPendingBySellerAdminId(
            String sellerAdminId) {
        SellerAdminAuthOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminAuthOutboxJpaEntity)
                        .where(
                                conditionBuilder.sellerAdminIdEq(sellerAdminId),
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
    public List<SellerAdminAuthOutboxJpaEntity> findPendingOutboxesForRetry(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(sellerAdminAuthOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(sellerAdminAuthOutboxJpaEntity.createdAt.asc())
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
    public List<SellerAdminAuthOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(sellerAdminAuthOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(sellerAdminAuthOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
