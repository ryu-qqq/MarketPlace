package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * 세토프 OrderResponse 호환 응답 DTO.
 *
 * @param orderId 주문 ID
 * @param buyerName 구매자 이름 (레거시 DB에 없음, 빈값)
 * @param payment 결제 정보
 * @param receiverInfo 수령인 정보
 * @param orderProduct 주문 상품 정보
 * @param orderDate 주문 일시
 */
public record LegacyOrderResponse(
        long orderId,
        String buyerName,
        LegacyPaymentInfo payment,
        LegacyReceiverInfo receiverInfo,
        LegacyOrderProductInfo orderProduct,
        Instant orderDate) {

    public record LegacyPaymentInfo(
            long paymentId, long amount, long commissionRate, long shareRatio) {}

    public record LegacyReceiverInfo(
            String receiverName,
            String phone,
            String address,
            String addressDetail,
            String zipCode,
            String deliveryRequest) {}

    public record LegacyOrderProductInfo(
            long productGroupId,
            long productId,
            String productGroupName,
            String brandName,
            String mainImageUrl,
            int quantity,
            String orderStatus,
            long regularPrice,
            long orderAmount,
            String option,
            List<String> options) {}
}
