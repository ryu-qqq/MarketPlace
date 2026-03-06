package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerAuthOutboxJpaEntity.sellerAuthOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerAuthOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerAuthOutboxQueryDslRepository - 셀러 인증 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerAuthOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerAuthOutboxConditionBuilder conditionBuilder;

    public SellerAuthOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, SellerAuthOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox 엔티티 (없으면 null)
     */
    public SellerAuthOutboxJpaEntity findById(Long outboxId) {
        return queryFactory
                .selectFrom(sellerAuthOutboxJpaEntity)
                .where(sellerAuthOutboxJpaEntity.id.eq(outboxId))
                .fetchOne();
    }

    /**
     * SellerId로 PENDING 상태의 Outbox 조회.
     *
     * @param sellerId 셀러 ID
     * @return Outbox Optional
     */
    public Optional<SellerAuthOutboxJpaEntity> findPendingBySellerId(Long sellerId) {
        SellerAuthOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAuthOutboxJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.statusPending())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 처리 대기 중인 Outbox 목록 조회 (스케줄러용).
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = PENDING
     *   <li>retry_count < max_retry
     *   <li>created_at < beforeTime (즉시 처리 대상 제외)
     * </ul>
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<SellerAuthOutboxJpaEntity> findPendingOutboxesForRetry(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(sellerAuthOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(sellerAuthOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회 (스케줄러용).
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = PROCESSING
     *   <li>updated_at < timeoutThreshold (좀비 상태)
     * </ul>
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<SellerAuthOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(sellerAuthOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(sellerAuthOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
