package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 상품주문 리스트 항목 결과 (V5).
 *
 * <p>주문 아이템 중심의 리스트 조회 결과입니다. 네이버 productOrder 패턴을 참고하여 order_item 단위로 주문/결제/배송/취소/클레임 정보를
 * 내포(nested)합니다.
 */
public record ProductOrderListResult(
        OrderInfo order,
        ProductOrderInfo productOrder,
        PaymentInfo payment,
        ReceiverInfo receiver,
        DeliveryInfo delivery,
        CancelSummary cancel,
        ClaimSummary claim) {

    /**
     * 주문 기본 정보.
     *
     * @param orderId 주문 ID
     * @param orderNumber 주문번호
     * @param salesChannelId 판매채널 ID
     * @param shopId 샵 ID
     * @param shopCode 샵 코드
     * @param shopName 샵 이름
     * @param externalOrderNo 외부 주문번호
     * @param externalOrderedAt 외부 주문일시
     * @param buyerName 구매자명
     * @param buyerEmail 구매자 이메일
     * @param buyerPhone 구매자 전화번호
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     */
    public record OrderInfo(
            String orderId,
            String orderNumber,
            long salesChannelId,
            long shopId,
            String shopCode,
            String shopName,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            Instant createdAt,
            Instant updatedAt) {}

    /**
     * 상품주문 정보 (주문 아이템 중심).
     *
     * @param orderItemId 주문 상품 ID
     * @param productGroupId 내부 상품그룹 ID
     * @param sellerId 셀러 ID
     * @param brandId 브랜드 ID
     * @param categoryId 카테고리 ID
     * @param productId 내부 상품 ID
     * @param skuCode SKU 코드
     * @param productGroupName 상품그룹명
     * @param brandName 브랜드명 (스냅샷)
     * @param sellerName 셀러명 (스냅샷)
     * @param mainImageUrl 대표 이미지 URL
     * @param externalProductId 외부 상품 ID
     * @param externalOptionId 외부 옵션 ID
     * @param externalProductName 외부 상품명
     * @param externalOptionName 외부 옵션명
     * @param externalImageUrl 외부 이미지 URL
     * @param regularPrice 정가
     * @param unitPrice 개당 판매가
     * @param quantity 수량
     * @param totalAmount 총 금액
     * @param discountAmount 할인 금액
     * @param paymentAmount 실결제 금액
     */
    public record ProductOrderInfo(
            Long orderItemId,
            String orderItemNumber,
            long productGroupId,
            Long sellerId,
            Long brandId,
            Long categoryId,
            long productId,
            String skuCode,
            String productGroupName,
            String brandName,
            String sellerName,
            String mainImageUrl,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int regularPrice,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int paymentAmount) {}

    /**
     * 결제 정보.
     *
     * @param paymentId 결제 ID (UUIDv7)
     * @param paymentNumber 결제 번호 (PAY-YYYYMMDD-XXXX)
     * @param paymentStatus 결제 상태
     * @param paymentMethod 결제 수단
     * @param paymentAgencyId PG사 결제 ID
     * @param paymentAmount 결제 금액
     * @param paidAt 결제일시
     * @param canceledAt 결제취소일시
     */
    public record PaymentInfo(
            String paymentId,
            String paymentNumber,
            String paymentStatus,
            String paymentMethod,
            String paymentAgencyId,
            int paymentAmount,
            Instant paidAt,
            Instant canceledAt) {}

    /**
     * 수령인 정보.
     *
     * @param receiverName 수령인명
     * @param receiverPhone 수령인 전화번호
     * @param receiverZipcode 수령인 우편번호
     * @param receiverAddress 수령인 주소
     * @param receiverAddressDetail 수령인 상세주소
     * @param deliveryRequest 배송 요청사항
     */
    public record ReceiverInfo(
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest) {}

    /**
     * 배송 정보.
     *
     * @param orderItemStatus 주문 상품 상태
     * @param externalOrderStatus 외부몰 주문 상태
     */
    public record DeliveryInfo(String orderItemStatus, String externalOrderStatus) {}

    /**
     * 취소 요약 정보.
     *
     * @param hasActiveCancel 활성 취소 존재 여부
     * @param totalCancelledQty 총 취소 수량
     * @param cancelableQty 취소 가능 수량
     * @param latest 최근 취소 정보
     */
    public record CancelSummary(
            boolean hasActiveCancel,
            int totalCancelledQty,
            int cancelableQty,
            LatestCancel latest) {

        /**
         * 최근 취소 정보.
         *
         * @param cancelId 취소 ID
         * @param cancelNumber 취소번호
         * @param status 취소 상태
         * @param qty 취소 수량
         * @param requestedAt 요청일시
         */
        public record LatestCancel(
                String cancelId,
                String cancelNumber,
                String status,
                int qty,
                Instant requestedAt) {}

        /** 취소 내역 없음. */
        public static CancelSummary none(int orderQuantity) {
            return new CancelSummary(false, 0, orderQuantity, null);
        }
    }

    /**
     * 클레임 요약 정보.
     *
     * @param hasActiveClaim 활성 클레임 존재 여부
     * @param activeCount 활성 클레임 수
     * @param totalClaimedQty 총 클레임 수량
     * @param claimableQty 클레임 가능 수량
     * @param latest 최근 클레임 정보
     */
    public record ClaimSummary(
            boolean hasActiveClaim,
            int activeCount,
            int totalClaimedQty,
            int claimableQty,
            LatestClaim latest) {

        /**
         * 최근 클레임 정보.
         *
         * @param claimId 클레임 ID
         * @param claimNumber 클레임번호
         * @param type 클레임 유형 (REFUND, EXCHANGE)
         * @param status 클레임 상태
         * @param qty 클레임 수량
         * @param requestedAt 요청일시
         */
        public record LatestClaim(
                String claimId,
                String claimNumber,
                String type,
                String status,
                int qty,
                Instant requestedAt) {}

        /** 클레임 내역 없음. */
        public static ClaimSummary none(int orderQuantity) {
            return new ClaimSummary(false, 0, 0, orderQuantity, null);
        }
    }
}
