package com.ryuqq.marketplace.application.inboundorder.port.in.command;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import java.util.List;

/**
 * 클레임 웹훅 수신 UseCase.
 *
 * <p>자사몰 취소/반품/철회 이벤트를 수신하여 ClaimSync 파이프라인으로 처리합니다.
 */
public interface ReceiveClaimWebhookUseCase {

    /**
     * 클레임 웹훅을 처리합니다.
     *
     * @param payloads 외부 클레임 데이터 목록
     * @param salesChannelId 판매채널 ID
     * @return 동기화 결과
     */
    ClaimSyncResult execute(List<ExternalClaimPayload> payloads, long salesChannelId);
}
