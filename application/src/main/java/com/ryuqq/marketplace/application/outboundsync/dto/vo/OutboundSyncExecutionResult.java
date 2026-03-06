package com.ryuqq.marketplace.application.outboundsync.dto.vo;

/**
 * 외부 채널 연동 실행 결과.
 *
 * @param success 성공 여부
 * @param externalProductId 외부 상품 ID (성공 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @param retryable 재시도 가능 여부 (실패 시)
 */
public record OutboundSyncExecutionResult(
        boolean success, String externalProductId, String errorMessage, boolean retryable) {

    /** 성공 결과 생성. */
    public static OutboundSyncExecutionResult success(String externalProductId) {
        return new OutboundSyncExecutionResult(true, externalProductId, null, false);
    }

    /** 실패 결과 생성. */
    public static OutboundSyncExecutionResult failure(String errorMessage, boolean retryable) {
        return new OutboundSyncExecutionResult(false, null, errorMessage, retryable);
    }
}
