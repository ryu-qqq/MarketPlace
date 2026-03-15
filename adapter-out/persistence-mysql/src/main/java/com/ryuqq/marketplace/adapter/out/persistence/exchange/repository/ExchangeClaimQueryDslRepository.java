package com.ryuqq.marketplace.adapter.out.persistence.exchange.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeItemJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 교환 클레임 QueryDSL Repository. */
@Repository
public class ExchangeClaimQueryDslRepository {

    private static final QExchangeClaimJpaEntity exchangeClaim =
            QExchangeClaimJpaEntity.exchangeClaimJpaEntity;
    private static final QExchangeItemJpaEntity exchangeItem =
            QExchangeItemJpaEntity.exchangeItemJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ExchangeClaimQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<ExchangeClaimJpaEntity> findById(String id) {
        ExchangeClaimJpaEntity entity =
                queryFactory.selectFrom(exchangeClaim).where(exchangeClaim.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExchangeItemJpaEntity> findItemsByClaimId(String claimId) {
        return queryFactory
                .selectFrom(exchangeItem)
                .where(exchangeItem.exchangeClaimId.eq(claimId))
                .fetch();
    }

    public Optional<ExchangeClaimJpaEntity> findByOrderId(String orderId) {
        ExchangeClaimJpaEntity entity =
                queryFactory
                        .selectFrom(exchangeClaim)
                        .where(exchangeClaim.orderId.eq(orderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExchangeClaimJpaEntity> findByOrderIds(List<String> orderIds) {
        return queryFactory
                .selectFrom(exchangeClaim)
                .where(exchangeClaim.orderId.in(orderIds))
                .fetch();
    }

    public List<ExchangeItemJpaEntity> findItemsByClaimIds(List<String> claimIds) {
        return queryFactory
                .selectFrom(exchangeItem)
                .where(exchangeItem.exchangeClaimId.in(claimIds))
                .fetch();
    }
}
