package com.ryuqq.marketplace.domain.externalproductsync.vo;

/**
 * 외부 상품 연동 Outbox 상태.
 *
 * <p>외부 판매채널로의 상품 연동 요청 처리 상태를 나타냅니다.
 */
public enum SyncStatus {

    /** 대기 중. 아직 처리되지 않은 상태. */
    PENDING("대기"),

    /** 처리 중. 현재 외부 API 호출 진행 중. */
    PROCESSING("처리중"),

    /** 완료. 외부 채널 연동 성공. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    SyncStatus(String description) {
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

    /** 다음 처리 상태로 전이. 이미 종료 상태인 경우 자기 자신을 반환. */
    public SyncStatus nextStatus() {
        return switch (this) {
            case PENDING -> PROCESSING;
            case PROCESSING -> COMPLETED;
            case COMPLETED, FAILED -> this;
        };
    }
}
