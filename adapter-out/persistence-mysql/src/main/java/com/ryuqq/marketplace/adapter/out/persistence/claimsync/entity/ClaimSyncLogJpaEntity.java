package com.ryuqq.marketplace.adapter.out.persistence.claimsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 클레임 동기화 로그 JPA 엔티티.
 *
 * <p>claim_sync_logs 테이블과 매핑됩니다. 외부 클레임 상태를 내부 클레임으로 처리한 이력을 기록합니다.
 */
@Entity
@Table(name = "claim_sync_logs")
public class ClaimSyncLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "external_product_order_id", nullable = false, length = 50)
    private String externalProductOrderId;

    @Column(name = "external_claim_type", nullable = false, length = 30)
    private String externalClaimType;

    @Column(name = "external_claim_status", nullable = false, length = 50)
    private String externalClaimStatus;

    @Column(name = "internal_claim_type", nullable = false, length = 20)
    private String internalClaimType;

    @Column(name = "internal_claim_id", nullable = false)
    private long internalClaimId;

    @Column(name = "action", nullable = false, length = 30)
    private String action;

    @Column(name = "synced_at", nullable = false, updatable = false)
    private Instant syncedAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected ClaimSyncLogJpaEntity() {}

    private ClaimSyncLogJpaEntity(
            Long id,
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            String internalClaimType,
            long internalClaimId,
            String action,
            Instant syncedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.externalProductOrderId = externalProductOrderId;
        this.externalClaimType = externalClaimType;
        this.externalClaimStatus = externalClaimStatus;
        this.internalClaimType = internalClaimType;
        this.internalClaimId = internalClaimId;
        this.action = action;
        this.syncedAt = syncedAt;
    }

    public static ClaimSyncLogJpaEntity create(
            Long id,
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            String internalClaimType,
            long internalClaimId,
            String action,
            Instant syncedAt) {
        return new ClaimSyncLogJpaEntity(
                id,
                salesChannelId,
                externalProductOrderId,
                externalClaimType,
                externalClaimStatus,
                internalClaimType,
                internalClaimId,
                action,
                syncedAt);
    }

    public Long getId() {
        return id;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalProductOrderId() {
        return externalProductOrderId;
    }

    public String getExternalClaimType() {
        return externalClaimType;
    }

    public String getExternalClaimStatus() {
        return externalClaimStatus;
    }

    public String getInternalClaimType() {
        return internalClaimType;
    }

    public long getInternalClaimId() {
        return internalClaimId;
    }

    public String getAction() {
        return action;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }
}
