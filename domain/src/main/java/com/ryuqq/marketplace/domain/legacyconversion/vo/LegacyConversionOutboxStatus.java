package com.ryuqq.marketplace.domain.legacyconversion.vo;

/**
 * 레거시 변환 Outbox 상태.
 *
 * <p>레거시 상품 → 내부 상품 변환 요청의 처리 상태를 나타냅니다.
 */
@SuppressWarnings("PMD.DataClass")
public enum LegacyConversionOutboxStatus {

    /** 대기 중. 아직 처리되지 않은 상태. */
    PENDING("대기"),

    /** 처리 중. 현재 변환 진행 중. */
    PROCESSING("처리중"),

    /** 완료. 내부 상품 변환 성공. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    LegacyConversionOutboxStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean canProcess() {
        return this == PENDING || this == PROCESSING;
    }

    /** 종료 상태(COMPLETED/FAILED) 여부 판별. */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
