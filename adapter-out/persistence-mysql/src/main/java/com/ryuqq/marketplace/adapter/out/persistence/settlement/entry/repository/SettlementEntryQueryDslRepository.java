package com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.QSettlementEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.SettlementEntryJpaEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 정산 원장 QueryDSL Repository. */
@Repository
public class SettlementEntryQueryDslRepository {

    private static final QSettlementEntryJpaEntity entry =
            QSettlementEntryJpaEntity.settlementEntryJpaEntity;

    private final JPAQueryFactory queryFactory;

    public SettlementEntryQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<SettlementEntryJpaEntity> findById(String id) {
        SettlementEntryJpaEntity entity =
                queryFactory.selectFrom(entry).where(entry.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    /** PENDING 상태이고 eligibleAt <= cutoffTime인 Entry 조회. */
    public List<SettlementEntryJpaEntity> findConfirmableEntries(Instant cutoffTime, int limit) {
        return queryFactory
                .selectFrom(entry)
                .where(entry.entryStatus.eq("PENDING"), entry.eligibleAt.loe(cutoffTime))
                .orderBy(entry.eligibleAt.asc())
                .limit(limit)
                .fetch();
    }

    /** 특정 셀러의 특정 상태 Entry 조회. */
    public List<SettlementEntryJpaEntity> findBySellerIdAndStatus(long sellerId, String status) {
        return queryFactory
                .selectFrom(entry)
                .where(entry.sellerId.eq(sellerId), entry.entryStatus.eq(status))
                .orderBy(entry.createdAt.asc())
                .fetch();
    }

    /** 특정 orderItemId의 Entry 목록. */
    public List<SettlementEntryJpaEntity> findByOrderItemId(String orderItemId) {
        return queryFactory
                .selectFrom(entry)
                .where(entry.orderItemId.eq(orderItemId))
                .orderBy(entry.createdAt.asc())
                .fetch();
    }

    /** ID 목록으로 Entry 조회. */
    public List<SettlementEntryJpaEntity> findByIdIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return queryFactory.selectFrom(entry).where(entry.id.in(ids)).fetch();
    }

    /** 상태·셀러 조건 기반 Entry 목록 조회 (페이징). */
    public List<SettlementEntryJpaEntity> findByCriteria(
            List<String> statuses, List<Long> sellerIds, int offset, int limit) {
        return queryFactory
                .selectFrom(entry)
                .where(statusIn(statuses), sellerIdIn(sellerIds))
                .orderBy(entry.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /** 상태·셀러 조건 기반 Entry 전체 건수. */
    public long countByCriteria(List<String> statuses, List<Long> sellerIds) {
        Long count =
                queryFactory
                        .select(entry.count())
                        .from(entry)
                        .where(statusIn(statuses), sellerIdIn(sellerIds))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /** 날짜별 Entry 집계 (eligible_at 기준, KST). */
    public List<Tuple> aggregateByDate(
            LocalDate startDate, LocalDate endDate, List<Long> sellerIds) {
        DateTemplate<LocalDate> dateExpr =
                Expressions.dateTemplate(
                        LocalDate.class,
                        "DATE(CONVERT_TZ({0}, '+00:00', '+09:00'))",
                        entry.eligibleAt);

        return queryFactory
                .select(
                        dateExpr,
                        entry.count(),
                        entry.salesAmount.sum(),
                        entry.commissionAmount.sum(),
                        entry.settlementAmount.sum())
                .from(entry)
                .where(dateExpr.goe(startDate), dateExpr.loe(endDate), sellerIdIn(sellerIds))
                .groupBy(dateExpr)
                .orderBy(dateExpr.asc())
                .fetch();
    }

    /** 지정 상태의 Entry가 존재하는 셀러 ID 목록 (중복 제거). */
    public List<Long> findDistinctSellerIdsByStatus(String status) {
        return queryFactory
                .select(entry.sellerId)
                .distinct()
                .from(entry)
                .where(entry.entryStatus.eq(status))
                .fetch();
    }

    private com.querydsl.core.types.dsl.BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return entry.entryStatus.in(statuses);
    }

    private com.querydsl.core.types.dsl.BooleanExpression sellerIdIn(List<Long> sellerIds) {
        if (sellerIds == null || sellerIds.isEmpty()) {
            return null;
        }
        return entry.sellerId.in(sellerIds);
    }
}
