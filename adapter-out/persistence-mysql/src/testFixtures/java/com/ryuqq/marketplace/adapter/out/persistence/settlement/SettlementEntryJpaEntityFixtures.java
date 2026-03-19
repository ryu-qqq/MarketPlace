package com.ryuqq.marketplace.adapter.out.persistence.settlement;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.SettlementEntryJpaEntity;
import java.time.Instant;

/** SettlementEntryJpaEntity 테스트 Fixtures. */
public final class SettlementEntryJpaEntityFixtures {

    private SettlementEntryJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01900000-0000-7000-9000-000000000001";
    public static final String DEFAULT_SETTLEMENT_ID = "01900000-0000-7000-8000-000000000001";
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_ORDER_ITEM_ID = "oi-test-fixture-001";
    public static final String DEFAULT_CLAIM_ID = "cancel-fixture-001";
    public static final String DEFAULT_ENTRY_TYPE_SALES = "SALES";
    public static final String DEFAULT_ENTRY_TYPE_CANCEL = "CANCEL";
    public static final String DEFAULT_ENTRY_TYPE_REFUND = "REFUND";
    public static final String DEFAULT_ENTRY_TYPE_ADJUSTMENT = "ADJUSTMENT";
    public static final String DEFAULT_ENTRY_STATUS_PENDING = "PENDING";
    public static final String DEFAULT_ENTRY_STATUS_CONFIRMED = "CONFIRMED";
    public static final String DEFAULT_ENTRY_STATUS_SETTLED = "SETTLED";
    public static final int DEFAULT_SALES_AMOUNT = 50000;
    public static final int DEFAULT_COMMISSION_RATE = 1000; // 10.00%
    public static final int DEFAULT_COMMISSION_AMOUNT = 5000;
    public static final int DEFAULT_SETTLEMENT_AMOUNT = 45000;

    // ===== Entity Fixtures =====

    /** SALES 타입, PENDING 상태 Entity 생성 (기본). */
    public static SettlementEntryJpaEntity salesPendingEntity() {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.plusSeconds(604800),
                now.minusSeconds(3600),
                now);
    }

    /** SALES 타입, PENDING 상태 Entity 생성 (ID 지정). */
    public static SettlementEntryJpaEntity salesPendingEntity(String id) {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.plusSeconds(604800),
                now.minusSeconds(3600),
                now);
    }

    /** SALES 타입, CONFIRMED 상태 Entity 생성. */
    public static SettlementEntryJpaEntity salesConfirmedEntity() {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_CONFIRMED,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.minusSeconds(86400),
                now.minusSeconds(3600),
                now);
    }

    /** SALES 타입, SETTLED 상태 Entity 생성 (settlementId 포함). */
    public static SettlementEntryJpaEntity salesSettledEntity() {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_SETTLED,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                DEFAULT_SETTLEMENT_ID,
                now.minusSeconds(86400),
                now.minusSeconds(3600),
                now);
    }

    /** CANCEL 타입, PENDING 상태 Entity 생성 (역분개). */
    public static SettlementEntryJpaEntity cancelReversalEntity() {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_CANCEL,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_CLAIM_ID,
                "CANCEL",
                null,
                null,
                now,
                now.minusSeconds(3600),
                now);
    }

    /** 특정 상태를 가진 SALES Entity 생성. */
    public static SettlementEntryJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                status,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.plusSeconds(604800),
                now.minusSeconds(3600),
                now);
    }

    /** 특정 orderItemId를 가진 PENDING Entity 생성. */
    public static SettlementEntryJpaEntity pendingEntityWithOrderItemId(
            String id, String orderItemId) {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                orderItemId,
                null,
                null,
                null,
                null,
                now.plusSeconds(604800),
                now.minusSeconds(3600),
                now);
    }

    /**
     * PENDING 상태이고 eligibleAt이 과거(이미 확정 가능)인 Entity 생성.
     *
     * <p>findConfirmableEntries 테스트용.
     */
    public static SettlementEntryJpaEntity eligiblePendingEntity(String id) {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.minusSeconds(86400),
                now.minusSeconds(3600),
                now);
    }

    /**
     * PENDING 상태이고 eligibleAt이 미래(아직 확정 불가)인 Entity 생성.
     *
     * <p>findConfirmableEntries 테스트용.
     */
    public static SettlementEntryJpaEntity notYetEligiblePendingEntity(String id) {
        Instant now = Instant.now();
        return SettlementEntryJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_ENTRY_TYPE_SALES,
                DEFAULT_ENTRY_STATUS_PENDING,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_COMMISSION_AMOUNT,
                DEFAULT_SETTLEMENT_AMOUNT,
                DEFAULT_ORDER_ITEM_ID,
                null,
                null,
                null,
                null,
                now.plusSeconds(604800),
                now.minusSeconds(3600),
                now);
    }
}
