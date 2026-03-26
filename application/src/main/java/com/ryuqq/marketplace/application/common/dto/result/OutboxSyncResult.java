package com.ryuqq.marketplace.application.common.dto.result;

/**
 * Outbox 기반 외부 API 동기화 결과.
 *
 * <p>Cancel/Refund/Exchange Outbox Strategy 실행 결과를 담습니다.
 *
 * @param success 성공 여부
 * @param retryable 재시도 가능 여부
 * @param errorMessage 오류 메시지
 */
public record OutboxSyncResult(boolean succeeded, boolean retryable, String errorMessage) {

    public static OutboxSyncResult success() {
        return new OutboxSyncResult(true, false, null);
    }

    public static OutboxSyncResult failure(boolean retryable, String errorMessage) {
        return new OutboxSyncResult(false, retryable, errorMessage);
    }

    public boolean isSuccess() {
        return succeeded;
    }
}
