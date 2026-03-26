package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 반품 요청 웹훅 요청.
 *
 * <p>배송 완료 후 구매자 반품 요청. ExternalClaimPayload(RETURN/RETURN_REQUEST)로 변환.
 *
 * @param salesChannelId 판매채널 ID
 * @param externalOrderId 외부 주문번호
 * @param items 반품 요청 아이템 목록
 */
public record ReturnRequestedWebhookRequest(
        @Positive long salesChannelId,
        @NotBlank String externalOrderId,
        @NotEmpty @Valid List<ReturnRequestedItemRequest> items) {

    /**
     * 반품 요청 아이템.
     *
     * @param externalProductOrderId 외부 상품주문 ID
     * @param returnReason 반품 사유
     * @param returnDetailedReason 반품 상세 사유
     * @param returnQuantity 반품 수량
     * @param collectDeliveryCompany 수거 택배사
     * @param collectTrackingNumber 수거 송장번호
     */
    public record ReturnRequestedItemRequest(
            @NotBlank String externalProductOrderId,
            String returnReason,
            String returnDetailedReason,
            @Positive int returnQuantity,
            String collectDeliveryCompany,
            String collectTrackingNumber) {}
}
