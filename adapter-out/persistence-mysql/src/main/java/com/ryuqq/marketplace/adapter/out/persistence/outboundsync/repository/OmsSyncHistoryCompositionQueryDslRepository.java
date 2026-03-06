package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.QOutboundProductJpaEntity.outboundProductJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.QSellerSalesChannelJpaEntity.sellerSalesChannelJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity.shopJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition.OutboundSyncOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * OmsSyncHistoryCompositionQueryDslRepository - 연동 이력 Composition QueryDSL 레포지토리.
 *
 * <p>outbound_sync_outboxes LEFT JOIN seller_sales_channels LEFT JOIN shop LEFT JOIN
 * outbound_products.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class OmsSyncHistoryCompositionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OutboundSyncOutboxConditionBuilder conditionBuilder;

    public OmsSyncHistoryCompositionQueryDslRepository(
            JPAQueryFactory queryFactory, OutboundSyncOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 검색 조건에 맞는 연동 이력 Composite 조회.
     *
     * @param criteria 검색 조건
     * @return SyncHistoryCompositeDto 목록
     */
    public List<SyncHistoryCompositeDto> findByCriteria(SyncHistorySearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                SyncHistoryCompositeDto.class,
                                outboundSyncOutboxJpaEntity.id,
                                shopJpaEntity.shopName,
                                shopJpaEntity.accountId,
                                outboundSyncOutboxJpaEntity.status.stringValue(),
                                outboundSyncOutboxJpaEntity.retryCount,
                                outboundSyncOutboxJpaEntity.errorMessage,
                                outboundProductJpaEntity.externalProductId,
                                outboundSyncOutboxJpaEntity.createdAt,
                                outboundSyncOutboxJpaEntity.processedAt))
                .from(outboundSyncOutboxJpaEntity)
                .leftJoin(sellerSalesChannelJpaEntity)
                .on(
                        sellerSalesChannelJpaEntity
                                .salesChannelId
                                .eq(outboundSyncOutboxJpaEntity.salesChannelId)
                                .and(
                                        sellerSalesChannelJpaEntity.sellerId.eq(
                                                outboundSyncOutboxJpaEntity.sellerId)))
                .leftJoin(shopJpaEntity)
                .on(shopJpaEntity.id.eq(sellerSalesChannelJpaEntity.shopId))
                .leftJoin(outboundProductJpaEntity)
                .on(
                        outboundProductJpaEntity
                                .productGroupId
                                .eq(outboundSyncOutboxJpaEntity.productGroupId)
                                .and(
                                        outboundProductJpaEntity.salesChannelId.eq(
                                                outboundSyncOutboxJpaEntity.salesChannelId)))
                .where(
                        conditionBuilder.productGroupIdEq(criteria.productGroupId()),
                        buildStatusCondition(criteria))
                .orderBy(outboundSyncOutboxJpaEntity.createdAt.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건에 맞는 전체 건수 조회.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    public long countByCriteria(SyncHistorySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(outboundSyncOutboxJpaEntity.count())
                        .from(outboundSyncOutboxJpaEntity)
                        .where(
                                conditionBuilder.productGroupIdEq(criteria.productGroupId()),
                                buildStatusCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanExpression buildStatusCondition(SyncHistorySearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        return conditionBuilder.statusEqEnum(
                OutboundSyncOutboxJpaEntity.Status.valueOf(criteria.statusFilter().name()));
    }
}
