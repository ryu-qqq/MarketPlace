package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 클레임 이력 JPA 엔티티.
 *
 * <p>claim_histories 테이블과 매핑됩니다. Cancel/Refund/Exchange 3개 클레임 타입의 상태 변경 및 수기 메모 이력을 기록합니다.
 */
@Entity
@Table(name = "claim_histories")
public class ClaimHistoryJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "claim_type", nullable = false, length = 20)
    private String claimType;

    @Column(name = "claim_id", nullable = false, length = 36)
    private String claimId;

    @Column(name = "history_type", nullable = false, length = 20)
    private String historyType;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "actor_type", nullable = false, length = 20)
    private String actorType;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "actor_name", nullable = false, length = 100)
    private String actorName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected ClaimHistoryJpaEntity() {}

    private ClaimHistoryJpaEntity(
            String id,
            String claimType,
            String claimId,
            String historyType,
            String title,
            String message,
            String actorType,
            String actorId,
            String actorName,
            Instant createdAt) {
        this.id = id;
        this.claimType = claimType;
        this.claimId = claimId;
        this.historyType = historyType;
        this.title = title;
        this.message = message;
        this.actorType = actorType;
        this.actorId = actorId;
        this.actorName = actorName;
        this.createdAt = createdAt;
    }

    public static ClaimHistoryJpaEntity create(
            String id,
            String claimType,
            String claimId,
            String historyType,
            String title,
            String message,
            String actorType,
            String actorId,
            String actorName,
            Instant createdAt) {
        return new ClaimHistoryJpaEntity(
                id,
                claimType,
                claimId,
                historyType,
                title,
                message,
                actorType,
                actorId,
                actorName,
                createdAt);
    }

    public String getId() {
        return id;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getHistoryType() {
        return historyType;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getActorType() {
        return actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
