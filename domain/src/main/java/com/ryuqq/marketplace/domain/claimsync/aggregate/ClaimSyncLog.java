package com.ryuqq.marketplace.domain.claimsync.aggregate;

import com.ryuqq.marketplace.domain.claimsync.id.ClaimSyncLogId;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import java.time.Instant;

/** 클레임 폴링 동기화 로그 Aggregate Root. 외부몰 클레임 상태를 내부 도메인으로 변환한 결과를 기록. */
public class ClaimSyncLog {

    private final ClaimSyncLogId id;
    private final long salesChannelId;
    private final String externalProductOrderId;
    private final String externalClaimType;
    private final String externalClaimStatus;
    private final String internalClaimType;
    private final long internalClaimId;
    private final ClaimSyncAction action;
    private final Instant syncedAt;

    private ClaimSyncLog(
            ClaimSyncLogId id,
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            String internalClaimType,
            long internalClaimId,
            ClaimSyncAction action,
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

    public static ClaimSyncLog forNew(
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            String internalClaimType,
            long internalClaimId,
            ClaimSyncAction action,
            Instant now) {
        validate(externalProductOrderId, externalClaimType, externalClaimStatus, action);
        return new ClaimSyncLog(
                ClaimSyncLogId.forNew(),
                salesChannelId,
                externalProductOrderId,
                externalClaimType,
                externalClaimStatus,
                internalClaimType,
                internalClaimId,
                action,
                now);
    }

    public static ClaimSyncLog reconstitute(
            ClaimSyncLogId id,
            long salesChannelId,
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            String internalClaimType,
            long internalClaimId,
            ClaimSyncAction action,
            Instant syncedAt) {
        return new ClaimSyncLog(
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

    private static void validate(
            String externalProductOrderId,
            String externalClaimType,
            String externalClaimStatus,
            ClaimSyncAction action) {
        if (externalProductOrderId == null || externalProductOrderId.isBlank()) {
            throw new IllegalArgumentException("externalProductOrderId는 null 또는 빈 문자열일 수 없습니다");
        }
        if (externalClaimType == null || externalClaimType.isBlank()) {
            throw new IllegalArgumentException("externalClaimType은 null 또는 빈 문자열일 수 없습니다");
        }
        if (externalClaimStatus == null || externalClaimStatus.isBlank()) {
            throw new IllegalArgumentException("externalClaimStatus는 null 또는 빈 문자열일 수 없습니다");
        }
        if (action == null) {
            throw new IllegalArgumentException("action은 null일 수 없습니다");
        }
    }

    public ClaimSyncLogId id() { return id; }
    public long idValue() { return id.value(); }
    public long salesChannelId() { return salesChannelId; }
    public String externalProductOrderId() { return externalProductOrderId; }
    public String externalClaimType() { return externalClaimType; }
    public String externalClaimStatus() { return externalClaimStatus; }
    public String internalClaimType() { return internalClaimType; }
    public long internalClaimId() { return internalClaimId; }
    public ClaimSyncAction action() { return action; }
    public Instant syncedAt() { return syncedAt; }
}
