package com.ryuqq.marketplace.application.inboundorder.port.in.command;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import java.util.List;

/**
 * 주문 생성 웹훅 수신 UseCase.
 *
 * <p>자사몰 결제 완료 이벤트를 수신하여 InboundOrder 파이프라인으로 처리합니다.
 */
public interface ReceiveOrderCreatedWebhookUseCase {

    /**
     * 주문 생성 웹훅을 처리합니다.
     *
     * @param payloads 외부 주문 데이터 목록
     * @param salesChannelId 판매채널 ID
     * @param shopId 샵 ID
     * @return 처리 결과
     */
    InboundOrderPollingResult execute(
            List<ExternalOrderPayload> payloads, long salesChannelId, long shopId);
}
