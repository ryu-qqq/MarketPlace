package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 반품 철회 웹훅 요청.
 *
 * <p>구매자가 반품 요청을 취소. ExternalClaimPayload(RETURN/RETURN_REJECT)로 변환되어 RefundClaimSyncHandler에서
 * REFUND_REJECTED 액션으로 처리.
 *
 * @param salesChannelId 판매채널 ID
 * @param externalOrderId 외부 주문번호
 * @param items 반품 철회 아이템 목록
 */
public record ReturnWithdrawnWebhookRequest(
        @Positive long salesChannelId,
        @NotBlank String externalOrderId,
        @NotEmpty @Valid List<ReturnWithdrawnItemRequest> items) {

    /**
     * 반품 철회 아이템.
     *
     * @param externalProductOrderId 외부 상품주문 ID
     */
    public record ReturnWithdrawnItemRequest(@NotBlank String externalProductOrderId) {}
}
