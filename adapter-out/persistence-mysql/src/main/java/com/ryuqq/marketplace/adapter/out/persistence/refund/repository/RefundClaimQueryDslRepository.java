package com.ryuqq.marketplace.adapter.out.persistence.refund.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundItemJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 환불 클레임 QueryDSL Repository. */
@Repository
public class RefundClaimQueryDslRepository {

    private static final QRefundClaimJpaEntity refundClaim =
            QRefundClaimJpaEntity.refundClaimJpaEntity;
    private static final QRefundItemJpaEntity refundItem = QRefundItemJpaEntity.refundItemJpaEntity;

    private final JPAQueryFactory queryFactory;

    public RefundClaimQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<RefundClaimJpaEntity> findById(String id) {
        RefundClaimJpaEntity entity =
                queryFactory.selectFrom(refundClaim).where(refundClaim.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<RefundItemJpaEntity> findItemsByClaimId(String claimId) {
        return queryFactory
                .selectFrom(refundItem)
                .where(refundItem.refundClaimId.eq(claimId))
                .fetch();
    }

    public Optional<RefundClaimJpaEntity> findByOrderId(String orderId) {
        RefundClaimJpaEntity entity =
                queryFactory
                        .selectFrom(refundClaim)
                        .where(refundClaim.orderId.eq(orderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<RefundClaimJpaEntity> findByOrderIds(List<String> orderIds) {
        return queryFactory.selectFrom(refundClaim).where(refundClaim.orderId.in(orderIds)).fetch();
    }

    public List<RefundItemJpaEntity> findItemsByClaimIds(List<String> claimIds) {
        return queryFactory
                .selectFrom(refundItem)
                .where(refundItem.refundClaimId.in(claimIds))
                .fetch();
    }
}
