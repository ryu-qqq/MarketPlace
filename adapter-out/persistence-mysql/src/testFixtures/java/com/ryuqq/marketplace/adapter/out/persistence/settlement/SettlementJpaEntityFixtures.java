package com.ryuqq.marketplace.adapter.out.persistence.settlement;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import java.time.Instant;
import java.time.LocalDate;

/** SettlementJpaEntity 테스트 Fixtures. */
public final class SettlementJpaEntityFixtures {

    private SettlementJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01900000-0000-7000-8000-000000000001";
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_STATUS_CALCULATING = "CALCULATING";
    public static final String DEFAULT_STATUS_CONFIRMED = "CONFIRMED";
    public static final String DEFAULT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_STATUS_HOLD = "HOLD";
    public static final String DEFAULT_STATUS_PAYOUT_REQUESTED = "PAYOUT_REQUESTED";
    public static final String DEFAULT_CYCLE_WEEKLY = "WEEKLY";
    public static final String DEFAULT_CYCLE_MONTHLY = "MONTHLY";
    public static final int DEFAULT_TOTAL_SALES_AMOUNT = 100000;
    public static final int DEFAULT_TOTAL_COMMISSION_AMOUNT = 10000;
    public static final int DEFAULT_TOTAL_REVERSAL_AMOUNT = 5000;
    public static final int DEFAULT_NET_SETTLEMENT_AMOUNT = 85000;
    public static final int DEFAULT_ENTRY_COUNT = 5;
    public static final String DEFAULT_HOLD_REASON = "이상 거래 의심으로 인한 보류";

    private static final LocalDate DEFAULT_PERIOD_START = LocalDate.now().minusDays(7);
    private static final LocalDate DEFAULT_PERIOD_END = LocalDate.now();
    private static final LocalDate DEFAULT_EXPECTED_SETTLEMENT_DAY = LocalDate.now().plusDays(14);

    // ===== Entity Fixtures =====

    /** CALCULATING 상태 Entity 생성 (기본). */
    public static SettlementJpaEntity calculatingEntity() {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_CALCULATING,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }

    /** CALCULATING 상태 Entity 생성 (ID 지정). */
    public static SettlementJpaEntity calculatingEntity(String id) {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_CALCULATING,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }

    /** CONFIRMED 상태 Entity 생성. */
    public static SettlementJpaEntity confirmedEntity() {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_CONFIRMED,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }

    /** COMPLETED 상태 Entity 생성. */
    public static SettlementJpaEntity completedEntity() {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_COMPLETED,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                LocalDate.now(),
                now.minusSeconds(3600),
                now);
    }

    /** HOLD 상태 Entity 생성. */
    public static SettlementJpaEntity holdEntity() {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_HOLD,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                DEFAULT_HOLD_REASON,
                now.minusSeconds(600),
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }

    /** 특정 상태를 가진 Entity 생성. */
    public static SettlementJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                status,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }

    /** 특정 sellerId를 가진 CALCULATING Entity 생성. */
    public static SettlementJpaEntity calculatingEntityWithSeller(String id, long sellerId) {
        Instant now = Instant.now();
        return SettlementJpaEntity.create(
                id,
                sellerId,
                DEFAULT_STATUS_CALCULATING,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_CYCLE_WEEKLY,
                DEFAULT_TOTAL_SALES_AMOUNT,
                DEFAULT_TOTAL_COMMISSION_AMOUNT,
                DEFAULT_TOTAL_REVERSAL_AMOUNT,
                DEFAULT_NET_SETTLEMENT_AMOUNT,
                DEFAULT_ENTRY_COUNT,
                null,
                null,
                DEFAULT_EXPECTED_SETTLEMENT_DAY,
                null,
                now.minusSeconds(3600),
                now);
    }
}
