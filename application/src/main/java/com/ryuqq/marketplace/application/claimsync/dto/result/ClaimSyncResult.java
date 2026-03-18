package com.ryuqq.marketplace.application.claimsync.dto.result;

/**
 * 클레임 동기화 결과.
 *
 * @param totalProcessed 전체 처리 건수
 * @param cancelSynced 취소 동기화 건수
 * @param refundSynced 반품 동기화 건수
 * @param exchangeSynced 교환 동기화 건수
 * @param skipped 스킵 건수
 * @param failed 실패 건수
 */
public record ClaimSyncResult(
        int totalProcessed,
        int cancelSynced,
        int refundSynced,
        int exchangeSynced,
        int skipped,
        int failed) {

    public static ClaimSyncResult empty() {
        return new ClaimSyncResult(0, 0, 0, 0, 0, 0);
    }

    public ClaimSyncResult merge(ClaimSyncResult other) {
        return new ClaimSyncResult(
                this.totalProcessed + other.totalProcessed,
                this.cancelSynced + other.cancelSynced,
                this.refundSynced + other.refundSynced,
                this.exchangeSynced + other.exchangeSynced,
                this.skipped + other.skipped,
                this.failed + other.failed);
    }
}
