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
}
