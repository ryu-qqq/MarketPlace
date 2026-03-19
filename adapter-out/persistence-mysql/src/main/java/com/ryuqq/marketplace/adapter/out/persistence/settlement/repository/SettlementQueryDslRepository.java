package com.ryuqq.marketplace.adapter.out.persistence.settlement.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.QSettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 정산 QueryDSL Repository. */
@Repository
public class SettlementQueryDslRepository {

    private static final QSettlementJpaEntity settlement = QSettlementJpaEntity.settlementJpaEntity;

    private final JPAQueryFactory queryFactory;

    public SettlementQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<SettlementJpaEntity> findById(String id) {
        SettlementJpaEntity entity =
                queryFactory.selectFrom(settlement).where(settlement.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<SettlementJpaEntity> findBySellerIdAndPeriod(
            long sellerId, LocalDate startDate, LocalDate endDate) {
        SettlementJpaEntity entity =
                queryFactory
                        .selectFrom(settlement)
                        .where(
                                settlement.sellerId.eq(sellerId),
                                settlement.periodStartDate.eq(startDate),
                                settlement.periodEndDate.eq(endDate))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SettlementJpaEntity> findBySellerIdAndStatus(long sellerId, String status) {
        return queryFactory
                .selectFrom(settlement)
                .where(settlement.sellerId.eq(sellerId), settlement.settlementStatus.eq(status))
                .orderBy(settlement.createdAt.desc())
                .fetch();
    }

    public List<SettlementJpaEntity> findByStatus(String status) {
        return queryFactory
                .selectFrom(settlement)
                .where(settlement.settlementStatus.eq(status))
                .orderBy(settlement.createdAt.desc())
                .fetch();
    }
}
