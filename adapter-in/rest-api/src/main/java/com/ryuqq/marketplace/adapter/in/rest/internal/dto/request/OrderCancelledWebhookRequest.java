package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 주문 취소 웹훅 요청.
 *
 * <p>자사몰 즉시 취소 시 호출. ExternalClaimPayload(CANCEL/CANCEL_REQUEST)로 변환.
 *
 * @param salesChannelId 판매채널 ID
 * @param externalOrderId 외부 주문번호
 * @param items 취소 아이템 목록
 */
public record OrderCancelledWebhookRequest(
        @Positive long salesChannelId,
        @NotBlank String externalOrderId,
        @NotEmpty @Valid List<CancelledItemRequest> items) {

    /**
     * 취소 아이템 요청.
     *
     * @param externalProductOrderId 외부 상품주문 ID
     * @param cancelReason 취소 사유
     * @param cancelDetailedReason 취소 상세 사유
     * @param cancelQuantity 취소 수량
     */
    public record CancelledItemRequest(
            @NotBlank String externalProductOrderId,
            String cancelReason,
            String cancelDetailedReason,
            @Positive int cancelQuantity) {}
}
