package com.ryuqq.marketplace.domain.productintelligence.vo;

/**
 * Intelligence Pipeline Outbox 상태.
 *
 * <p>Outbox Relay 기반 파이프라인 상태 흐름: PENDING → SENT → COMPLETED / FAILED
 */
public enum IntelligenceOutboxStatus {

    /** 대기 중. Outbox Relay가 SQS로 발행하기 전 상태. */
    PENDING("대기"),

    /** 발행 완료. 3개 Analyzer 큐에 메시지가 발행된 상태. */
    SENT("발행완료"),

    /** 완료. 모든 Analyzer 큐 발행 성공. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    IntelligenceOutboxStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isSent() {
        return this == SENT;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    /** PENDING 상태에서만 SENT로 전환 가능. Outbox Relay에서 사용. */
    public boolean canSend() {
        return this == PENDING;
    }

    /** 종료 상태(COMPLETED/FAILED) 여부 판별. */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    /** 진행 중(SENT) 상태 여부. 타임아웃 복구 대상. */
    public boolean isInProgress() {
        return this == SENT;
    }
}
