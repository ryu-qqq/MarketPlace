package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 구매 확정 웹훅 요청.
 *
 * <p>배송 완료 후 자동/수동 구매 확정. ExternalOrderItemMapping 역조회 후 ConfirmOrderUseCase 호출.
 *
 * @param salesChannelId 판매채널 ID
 * @param externalOrderId 외부 주문번호
 * @param items 구매 확정 아이템 목록
 */
public record PurchaseConfirmedWebhookRequest(
        @Positive long salesChannelId,
        @NotBlank String externalOrderId,
        @NotEmpty @Valid List<PurchaseConfirmedItemRequest> items) {

    /**
     * 구매 확정 아이템.
     *
     * @param externalProductOrderId 외부 상품주문 ID
     */
    public record PurchaseConfirmedItemRequest(@NotBlank String externalProductOrderId) {}
}
