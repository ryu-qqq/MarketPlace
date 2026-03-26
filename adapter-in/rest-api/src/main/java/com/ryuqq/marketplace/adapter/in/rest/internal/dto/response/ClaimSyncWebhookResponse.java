package com.ryuqq.marketplace.adapter.in.rest.internal.dto.response;

import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;

/**
 * 클레임 동기화 웹훅 응답.
 *
 * @param totalProcessed 전체 처리 건수
 * @param cancelSynced 취소 동기화 건수
 * @param refundSynced 반품 동기화 건수
 * @param exchangeSynced 교환 동기화 건수
 * @param skipped 스킵 건수
 * @param failed 실패 건수
 */
public record ClaimSyncWebhookResponse(
        int totalProcessed,
        int cancelSynced,
        int refundSynced,
        int exchangeSynced,
        int skipped,
        int failed) {

    public static ClaimSyncWebhookResponse from(ClaimSyncResult result) {
        return new ClaimSyncWebhookResponse(
                result.totalProcessed(),
                result.cancelSynced(),
                result.refundSynced(),
                result.exchangeSynced(),
                result.skipped(),
                result.failed());
    }
}
