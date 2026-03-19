package com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** 정산 원장 JPA 엔티티. */
@Entity
@Table(name = "settlement_entries")
public class SettlementEntryJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "entry_type", nullable = false, length = 20)
    private String entryType;

    @Column(name = "entry_status", nullable = false, length = 20)
    private String entryStatus;

    @Column(name = "sales_amount", nullable = false)
    private int salesAmount;

    @Column(name = "commission_rate", nullable = false)
    private int commissionRate;

    @Column(name = "commission_amount", nullable = false)
    private int commissionAmount;

    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "claim_id", length = 36)
    private String claimId;

    @Column(name = "claim_type", length = 20)
    private String claimType;

    @Column(name = "reversal_of_entry_id", length = 36)
    private String reversalOfEntryId;

    @Column(name = "settlement_id", length = 36)
    private String settlementId;

    @Column(name = "eligible_at")
    private Instant eligibleAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected SettlementEntryJpaEntity() {
        super();
    }

    private SettlementEntryJpaEntity(
            String id,
            long sellerId,
            String entryType,
            String entryStatus,
            int salesAmount,
            int commissionRate,
            int commissionAmount,
            int settlementAmount,
            String orderItemId,
            String claimId,
            String claimType,
            String reversalOfEntryId,
            String settlementId,
            Instant eligibleAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.entryType = entryType;
        this.entryStatus = entryStatus;
        this.salesAmount = salesAmount;
        this.commissionRate = commissionRate;
        this.commissionAmount = commissionAmount;
        this.settlementAmount = settlementAmount;
        this.orderItemId = orderItemId;
        this.claimId = claimId;
        this.claimType = claimType;
        this.reversalOfEntryId = reversalOfEntryId;
        this.settlementId = settlementId;
        this.eligibleAt = eligibleAt;
    }

    public static SettlementEntryJpaEntity create(
            String id,
            long sellerId,
            String entryType,
            String entryStatus,
            int salesAmount,
            int commissionRate,
            int commissionAmount,
            int settlementAmount,
            String orderItemId,
            String claimId,
            String claimType,
            String reversalOfEntryId,
            String settlementId,
            Instant eligibleAt,
            Instant createdAt,
            Instant updatedAt) {
        return new SettlementEntryJpaEntity(
                id,
                sellerId,
                entryType,
                entryStatus,
                salesAmount,
                commissionRate,
                commissionAmount,
                settlementAmount,
                orderItemId,
                claimId,
                claimType,
                reversalOfEntryId,
                settlementId,
                eligibleAt,
                createdAt,
                updatedAt);
    }

    public String getId() {
        return id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public String getEntryType() {
        return entryType;
    }

    public String getEntryStatus() {
        return entryStatus;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public int getCommissionRate() {
        return commissionRate;
    }

    public int getCommissionAmount() {
        return commissionAmount;
    }

    public int getSettlementAmount() {
        return settlementAmount;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getReversalOfEntryId() {
        return reversalOfEntryId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public Instant getEligibleAt() {
        return eligibleAt;
    }
}
