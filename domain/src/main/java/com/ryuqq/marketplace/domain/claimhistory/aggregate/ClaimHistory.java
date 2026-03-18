package com.ryuqq.marketplace.domain.claimhistory.aggregate;

import com.ryuqq.marketplace.domain.claimhistory.id.ClaimHistoryId;
import com.ryuqq.marketplace.domain.claimhistory.vo.Actor;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.time.Instant;

/** 클레임 이력 Aggregate Root. Cancel/Refund/Exchange 3개 클레임 타입이 공통으로 사용합니다. */
public class ClaimHistory {

    private final ClaimHistoryId id;
    private final ClaimType claimType;
    private final String claimId;
    private final ClaimHistoryType historyType;
    private final String title;
    private final String message;
    private final Actor actor;
    private final Instant createdAt;

    private ClaimHistory(
            ClaimHistoryId id,
            ClaimType claimType,
            String claimId,
            ClaimHistoryType historyType,
            String title,
            String message,
            Actor actor,
            Instant createdAt) {
        this.id = id;
        this.claimType = claimType;
        this.claimId = claimId;
        this.historyType = historyType;
        this.title = title;
        this.message = message;
        this.actor = actor;
        this.createdAt = createdAt;
    }

    /** 상태 변경 이력 생성. */
    public static ClaimHistory forStatusChange(
            ClaimHistoryId id,
            ClaimType claimType,
            String claimId,
            String fromStatus,
            String toStatus,
            Actor actor,
            Instant now) {
        String title = resolveStatusChangeTitle(toStatus);
        String message = fromStatus + " → " + toStatus;
        return new ClaimHistory(id, claimType, claimId, ClaimHistoryType.STATUS_CHANGE, title, message, actor, now);
    }

    /** 수기 메모 이력 생성. */
    public static ClaimHistory forManual(
            ClaimHistoryId id,
            ClaimType claimType,
            String claimId,
            String message,
            Actor actor,
            Instant now) {
        return new ClaimHistory(id, claimType, claimId, ClaimHistoryType.MANUAL, "CS 메모", message, actor, now);
    }

    /** DB에서 복원. */
    public static ClaimHistory reconstitute(
            ClaimHistoryId id,
            ClaimType claimType,
            String claimId,
            ClaimHistoryType historyType,
            String title,
            String message,
            Actor actor,
            Instant createdAt) {
        return new ClaimHistory(id, claimType, claimId, historyType, title, message, actor, createdAt);
    }

    private static String resolveStatusChangeTitle(String toStatus) {
        return switch (toStatus) {
            case "REQUESTED" -> "요청";
            case "APPROVED" -> "승인";
            case "COLLECTING" -> "수거 시작";
            case "COLLECTED" -> "수거 완료";
            case "PREPARING" -> "준비 시작";
            case "SHIPPING" -> "재배송 시작";
            case "COMPLETED" -> "완료";
            case "REJECTED" -> "거절";
            case "CANCELLED" -> "취소";
            default -> "상태 변경";
        };
    }

    public ClaimHistoryId id() { return id; }
    public String idValue() { return id.value(); }
    public ClaimType claimType() { return claimType; }
    public String claimId() { return claimId; }
    public ClaimHistoryType historyType() { return historyType; }
    public String title() { return title; }
    public String message() { return message; }
    public Actor actor() { return actor; }
    public Instant createdAt() { return createdAt; }
}
