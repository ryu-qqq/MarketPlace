package com.ryuqq.marketplace.application.outboundproduct.dto.result;

/**
 * 수동 외부몰 전송 결과 DTO.
 *
 * @param createCount 신규 생성 Outbox 수
 * @param updateCount 수정 Outbox 수
 * @param skippedCount 중복/미연결로 스킵된 수
 * @param status 결과 상태 (ACCEPTED)
 */
public record ManualSyncResult(int createCount, int updateCount, int skippedCount, String status) {

    public static ManualSyncResult of(int createCount, int updateCount, int skippedCount) {
        return new ManualSyncResult(createCount, updateCount, skippedCount, "ACCEPTED");
    }

    public int totalOutboxCount() {
        return createCount + updateCount;
    }
}
