package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.QExternalProductSyncOutboxJpaEntity.externalProductSyncOutboxJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.condition.ExternalProductSyncOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.ExternalProductSyncOutboxJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ExternalProductSyncOutboxQueryDslRepository - 외부 상품 연동 Outbox QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class ExternalProductSyncOutboxQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ExternalProductSyncOutboxConditionBuilder conditionBuilder;

    public ExternalProductSyncOutboxQueryDslRepository(
            JPAQueryFactory queryFactory,
            ExternalProductSyncOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 상품그룹 ID로 PENDING 상태의 Outbox 목록 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return PENDING 상태의 Outbox 엔티티 목록
     */
    public List<ExternalProductSyncOutboxJpaEntity> findPendingByProductGroupId(
            Long productGroupId) {
        return queryFactory
                .selectFrom(externalProductSyncOutboxJpaEntity)
                .where(
                        conditionBuilder.productGroupIdEq(productGroupId),
                        conditionBuilder.statusPending())
                .fetch();
    }
}
