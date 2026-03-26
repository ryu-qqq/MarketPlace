package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;

/**
 * 주문 생성 웹훅 요청.
 *
 * <p>자사몰 결제 완료 시 호출. ExternalOrderPayload로 변환되어 InboundOrder 파이프라인 진입.
 *
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param externalOrderNo 외부 주문번호
 * @param orderedAt 주문일시
 * @param buyerName 구매자명
 * @param buyerEmail 구매자 이메일
 * @param buyerPhone 구매자 전화번호
 * @param paymentMethod 결제수단
 * @param totalPaymentAmount 총 결제금액
 * @param paidAt 결제일시
 * @param items 주문 아이템 목록
 */
public record OrderCreatedWebhookRequest(
        @Positive long salesChannelId,
        @Positive long shopId,
        @NotBlank String externalOrderNo,
        Instant orderedAt,
        String buyerName,
        String buyerEmail,
        String buyerPhone,
        String paymentMethod,
        int totalPaymentAmount,
        Instant paidAt,
        @NotEmpty @Valid List<OrderCreatedItemRequest> items) {

    /**
     * 주문 아이템 요청.
     *
     * @param externalProductOrderId 외부 상품주문 ID
     * @param externalProductId 외부 상품 ID
     * @param externalOptionId 외부 옵션 ID
     * @param externalProductName 외부 상품명
     * @param externalOptionName 외부 옵션명
     * @param externalImageUrl 외부 이미지 URL
     * @param unitPrice 개당 판매가
     * @param quantity 수량
     * @param totalAmount 합계 금액
     * @param discountAmount 할인 금액
     * @param paymentAmount 실결제 금액
     * @param receiverName 수령인명
     * @param receiverPhone 수령인 전화번호
     * @param receiverZipCode 우편번호
     * @param receiverAddress 주소
     * @param receiverAddressDetail 상세주소
     * @param deliveryRequest 배송 요청사항
     */
    public record OrderCreatedItemRequest(
            @NotBlank String externalProductOrderId,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            @Positive int quantity,
            int totalAmount,
            int discountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipCode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest) {}
}
