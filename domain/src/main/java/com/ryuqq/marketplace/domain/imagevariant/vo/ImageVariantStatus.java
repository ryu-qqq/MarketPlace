package com.ryuqq.marketplace.domain.imagevariant.vo;

/**
 * 이미지 Variant 처리 상태.
 *
 * <p>이미지 변환 전체 파이프라인의 상태를 나타냅니다.
 */
public enum ImageVariantStatus {
    PENDING("대기"),
    PROCESSING("처리중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    ImageVariantStatus(String description) {
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

    /** 종료 상태(COMPLETED/FAILED) 여부 판별. */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    /** 처리 가능한 상태인지 판별. */
    public boolean canProcess() {
        return this == PENDING;
    }

    /** 성공 시 다음 상태를 반환합니다. 종료 상태에서는 IllegalStateException. */
    public ImageVariantStatus nextOnSuccess() {
        return switch (this) {
            case PENDING -> PROCESSING;
            case PROCESSING -> COMPLETED;
            case COMPLETED, FAILED -> throw new IllegalStateException("종료 상태에서 전환 불가: " + this);
        };
    }
}
