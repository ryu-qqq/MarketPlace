package com.ryuqq.marketplace.domain.productgroupinspection.vo;

/** 상품 그룹 검수 Outbox 상태. SQS 기반 3단계 파이프라인에 맞춘 상태 흐름. */
@SuppressWarnings("PMD.DataClass")
public enum InspectionOutboxStatus {

    /** 대기 중. Outbox Relay가 SQS로 발행하기 전 상태. */
    PENDING("대기"),

    /** 발행 완료. SQS Scoring 큐에 메시지가 발행된 상태. */
    SENT("발행완료"),

    /** 채점 중. Scoring Consumer가 점수를 계산하는 중. */
    SCORING("채점중"),

    /** 보강 중. Enhancement Consumer가 LLM으로 미달 항목을 보강하는 중. */
    ENHANCING("보강중"),

    /** 검증 중. Verification Consumer가 LLM으로 최종 품질 검증 중. */
    VERIFYING("검증중"),

    /** 완료. 검수 완료. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    InspectionOutboxStatus(String description) {
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

    public boolean isScoring() {
        return this == SCORING;
    }

    public boolean isEnhancing() {
        return this == ENHANCING;
    }

    public boolean isVerifying() {
        return this == VERIFYING;
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

    /** 진행 중(SENT, SCORING, ENHANCING, VERIFYING) 상태 여부. 타임아웃 복구 대상. */
    public boolean isInProgress() {
        return this == SENT || this == SCORING || this == ENHANCING || this == VERIFYING;
    }
}
