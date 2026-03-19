package com.ryuqq.marketplace.adapter.out.persistence.settlement.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

/** 정산 JPA 엔티티. */
@Entity
@Table(name = "settlements")
public class SettlementJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "settlement_status", nullable = false, length = 30)
    private String settlementStatus;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "settlement_cycle", nullable = false, length = 20)
    private String settlementCycle;

    @Column(name = "total_sales_amount", nullable = false)
    private int totalSalesAmount;

    @Column(name = "total_commission_amount", nullable = false)
    private int totalCommissionAmount;

    @Column(name = "total_reversal_amount", nullable = false)
    private int totalReversalAmount;

    @Column(name = "net_settlement_amount", nullable = false)
    private int netSettlementAmount;

    @Column(name = "entry_count", nullable = false)
    private int entryCount;

    @Column(name = "hold_reason", length = 500)
    private String holdReason;

    @Column(name = "hold_at")
    private Instant holdAt;

    @Column(name = "expected_settlement_day")
    private LocalDate expectedSettlementDay;

    @Column(name = "settlement_day")
    private LocalDate settlementDay;

    protected SettlementJpaEntity() {
        super();
    }

    private SettlementJpaEntity(
            String id,
            long sellerId,
            String settlementStatus,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            String settlementCycle,
            int totalSalesAmount,
            int totalCommissionAmount,
            int totalReversalAmount,
            int netSettlementAmount,
            int entryCount,
            String holdReason,
            Instant holdAt,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.settlementStatus = settlementStatus;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.settlementCycle = settlementCycle;
        this.totalSalesAmount = totalSalesAmount;
        this.totalCommissionAmount = totalCommissionAmount;
        this.totalReversalAmount = totalReversalAmount;
        this.netSettlementAmount = netSettlementAmount;
        this.entryCount = entryCount;
        this.holdReason = holdReason;
        this.holdAt = holdAt;
        this.expectedSettlementDay = expectedSettlementDay;
        this.settlementDay = settlementDay;
    }

    public static SettlementJpaEntity create(
            String id,
            long sellerId,
            String settlementStatus,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            String settlementCycle,
            int totalSalesAmount,
            int totalCommissionAmount,
            int totalReversalAmount,
            int netSettlementAmount,
            int entryCount,
            String holdReason,
            Instant holdAt,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant createdAt,
            Instant updatedAt) {
        return new SettlementJpaEntity(
                id,
                sellerId,
                settlementStatus,
                periodStartDate,
                periodEndDate,
                settlementCycle,
                totalSalesAmount,
                totalCommissionAmount,
                totalReversalAmount,
                netSettlementAmount,
                entryCount,
                holdReason,
                holdAt,
                expectedSettlementDay,
                settlementDay,
                createdAt,
                updatedAt);
    }

    public String getId() {
        return id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public String getSettlementCycle() {
        return settlementCycle;
    }

    public int getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public int getTotalCommissionAmount() {
        return totalCommissionAmount;
    }

    public int getTotalReversalAmount() {
        return totalReversalAmount;
    }

    public int getNetSettlementAmount() {
        return netSettlementAmount;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public String getHoldReason() {
        return holdReason;
    }

    public Instant getHoldAt() {
        return holdAt;
    }

    public LocalDate getExpectedSettlementDay() {
        return expectedSettlementDay;
    }

    public LocalDate getSettlementDay() {
        return settlementDay;
    }
}
