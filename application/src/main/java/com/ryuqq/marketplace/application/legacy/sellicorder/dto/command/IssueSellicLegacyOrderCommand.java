package com.ryuqq.marketplace.application.legacy.sellicorder.dto.command;

import java.time.Instant;

/**
 * 셀릭 주문 → luxurydb 레거시 저장용 커맨드.
 *
 * <p>셀릭 API 응답 데이터를 luxurydb의 payment, orders, shipment, settlement, external_order,
 * interlocking_order, payment_snapshot_shipping_address 등 테이블에 INSERT하기 위한 데이터 컨테이너.
 *
 * @param payment 결제 정보
 * @param order 주문 정보
 * @param shipment 배송 정보
 * @param settlement 정산 정보
 * @param externalOrder 외부 주문 연결 정보
 * @param interlockingOrder 연동 주문 정보
 * @param shippingAddress 수령인 주소 정보
 */
public record IssueSellicLegacyOrderCommand(
        Payment payment,
        Order order,
        Shipment shipment,
        Settlement settlement,
        ExternalOrder externalOrder,
        InterlockingOrder interlockingOrder,
        ShippingAddress shippingAddress) {

    /**
     * 결제 정보.
     *
     * @param userId 사용자 ID (셀릭 주문은 시스템 사용자 1L)
     * @param paymentAmount 결제 금액
     * @param paymentStatus 결제 상태 (PAYMENT_COMPLETED)
     * @param siteName 사이트명 (SEWON)
     * @param paymentDate 결제 일시
     * @param buyerName 구매자명
     * @param buyerEmail 구매자 이메일
     * @param buyerPhone 구매자 전화번호
     * @param paymentUniqueId 결제 고유 ID (SEWON_{MALL_ID}_{ORDER_ID})
     * @param paymentChannel 결제 채널 (PC)
     */
    public record Payment(
            long userId,
            long paymentAmount,
            String paymentStatus,
            String siteName,
            Instant paymentDate,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentUniqueId,
            String paymentChannel) {}

    /**
     * 주문 정보.
     *
     * @param productId 상품 ID (luxurydb product.PRODUCT_ID)
     * @param sellerId 셀러 ID
     * @param userId 사용자 ID (시스템 사용자 1L)
     * @param orderAmount 주문 금액
     * @param orderStatus 주문 상태 (PAYMENT_COMPLETED)
     * @param quantity 주문 수량
     */
    public record Order(
            long productId,
            long sellerId,
            long userId,
            long orderAmount,
            String orderStatus,
            int quantity) {}

    /**
     * 배송 정보.
     *
     * @param senderName 발송인명
     * @param senderEmail 발송인 이메일
     * @param senderPhone 발송인 전화번호
     * @param deliveryStatus 배송 상태 (DELIVERY_PENDING)
     */
    public record Shipment(
            String senderName, String senderEmail, String senderPhone, String deliveryStatus) {}

    /**
     * 정산 정보.
     *
     * @param sellerCommissionRate 셀러 수수료율
     */
    public record Settlement(long sellerCommissionRate) {}

    /**
     * 외부 주문 연결 정보.
     *
     * @param siteId 사이트 ID (셀릭 external_order.SITE_ID)
     * @param externalIdx 셀릭 IDX
     * @param externalOrderPkId 외부 주문 PK (ORDER_ID_SUB_ID)
     */
    public record ExternalOrder(long siteId, long externalIdx, String externalOrderPkId) {}

    /**
     * 연동 주문 정보.
     *
     * @param interlockingSiteId 연동 사이트 ID (2 = SELLIC)
     * @param siteName 사이트명 (SEWON)
     * @param externalIdx 셀릭 IDX
     * @param externalOrderId 외부 주문 번호
     */
    public record InterlockingOrder(
            long interlockingSiteId, String siteName, long externalIdx, String externalOrderId) {}

    /**
     * 수령인 주소 정보.
     *
     * @param receiverName 수취인명
     * @param phoneNumber 수취인 전화번호
     * @param addressLine1 배송 주소
     * @param zipCode 우편번호
     * @param deliveryRequest 배송 메시지
     */
    public record ShippingAddress(
            String receiverName,
            String phoneNumber,
            String addressLine1,
            String zipCode,
            String deliveryRequest) {}
}
