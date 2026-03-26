package com.ryuqq.marketplace.domain.imagevariantsync.vo;

/**
 * 이미지 Variant Sync Outbox 상태.
 *
 * <p>이미지 Variant 동기화 요청의 처리 상태를 나타냅니다. PROCESSING 없이 동기 호출 구조로 동작합니다.
 */
@SuppressWarnings("PMD.DataClass")
public enum ImageVariantSyncOutboxStatus {

    /** 대기 중. 아직 동기화되지 않은 상태. */
    PENDING("대기"),

    /** 완료. 세토프 Sync API 호출 완료. */
    COMPLETED("완료"),

    /** 실패. 최대 재시도 횟수 초과. */
    FAILED("실패");

    private final String description;

    ImageVariantSyncOutboxStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
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
}
