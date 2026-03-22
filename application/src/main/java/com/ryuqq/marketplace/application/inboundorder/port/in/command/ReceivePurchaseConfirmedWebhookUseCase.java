package com.ryuqq.marketplace.application.inboundorder.port.in.command;

import java.util.List;

/**
 * 구매 확정 웹훅 수신 UseCase.
 *
 * <p>자사몰 구매 확정 이벤트를 수신하여 내부 주문상품 상태를 CONFIRMED로 전환합니다. 이미 CONFIRMED인 항목은 무시합니다.
 */
public interface ReceivePurchaseConfirmedWebhookUseCase {

    /**
     * 구매 확정 웹훅을 처리합니다.
     *
     * @param salesChannelId 판매채널 ID
     * @param externalProductOrderIds 외부 상품주문 ID 목록
     */
    void execute(long salesChannelId, List<String> externalProductOrderIds);
}
