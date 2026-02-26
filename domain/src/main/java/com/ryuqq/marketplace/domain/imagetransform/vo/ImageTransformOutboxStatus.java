package com.ryuqq.marketplace.domain.imagetransform.vo;

/**
 * 이미지 변환 Outbox 상태.
 *
 * <p>이미지 변환 요청의 처리 상태를 나타냅니다.
 */
public enum ImageTransformOutboxStatus {

    /** 대기 중. 아직 변환 요청이 생성되지 않은 상태. */
    PENDING("대기"),

    /** 처리 중. FileFlow에 변환 요청이 전송되어 처리 중인 상태. */
    PROCESSING("처리중"),

    /** 완료. 변환 완료 및 ImageVariant 생성 완료. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    ImageTransformOutboxStatus(String description) {
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

    /** 성공 시 다음 상태를 반환합니다. 종료 상태에서는 IllegalStateException. */
    public ImageTransformOutboxStatus nextOnSuccess() {
        return switch (this) {
            case PENDING -> PROCESSING;
            case PROCESSING -> COMPLETED;
            case COMPLETED, FAILED -> throw new IllegalStateException("종료 상태에서 전환 불가: " + this);
        };
    }
}
