package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminEmailOutboxJpaEntity.sellerAdminEmailOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminEmailOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerAdminEmailOutboxQueryDslRepository - 셀러 관리자 이메일 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerAdminEmailOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerAdminEmailOutboxConditionBuilder conditionBuilder;

    public SellerAdminEmailOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, SellerAdminEmailOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * SellerId로 PENDING 상태의 이메일 Outbox 조회.
     *
     * @param sellerId 셀러 ID
     * @return Outbox Optional
     */
    public Optional<SellerAdminEmailOutboxJpaEntity> findPendingBySellerId(Long sellerId) {
        SellerAdminEmailOutboxJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminEmailOutboxJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.statusPending())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 처리 대기 중인 이메일 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<SellerAdminEmailOutboxJpaEntity> findPendingOutboxesForRetry(
            Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(sellerAdminEmailOutboxJpaEntity)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(sellerAdminEmailOutboxJpaEntity.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * PROCESSING 타임아웃 이메일 Outbox 목록 조회 (스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    public List<SellerAdminEmailOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(sellerAdminEmailOutboxJpaEntity)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(sellerAdminEmailOutboxJpaEntity.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
