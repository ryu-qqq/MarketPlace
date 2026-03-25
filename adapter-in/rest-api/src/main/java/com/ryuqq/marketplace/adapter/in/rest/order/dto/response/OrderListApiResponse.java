package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 상품주문 리스트 항목 응답 (V5).
 *
 * <p>네이버 productOrder 패턴: 리스트 1행 = 상품주문(order_item) 1건. 같은 주문의 상품들은 order.orderId로 그룹핑 가능.
 */
@Schema(description = "상품주문 리스트 항목")
public record OrderListApiResponse(
        @Schema(description = "소속 주문 정보") OrderInfoApiResponse order,
        @Schema(description = "상품주문 정보") ProductOrderApiResponse productOrder,
        @Schema(description = "결제 정보") PaymentInfoApiResponse payment,
        @Schema(description = "수령인/배송지 정보") ReceiverApiResponse receiver,
        @Schema(description = "배송 상태") DeliveryApiResponse delivery,
        @Schema(description = "취소 요약 (없으면 null)") CancelSummaryApiResponse cancel,
        @Schema(description = "클레임 요약 (없으면 null)") ClaimSummaryApiResponse claim) {

    /** 소속 주문 정보. */
    @Schema(description = "소속 주문 정보")
    public record OrderInfoApiResponse(
            @Schema(description = "주문 ID (UUIDv7)") String orderId,
            @Schema(description = "주문번호 (ORD-YYYYMMDD-XXXX)") String orderNumber,
            @Schema(description = "주문 상태") String status,
            @Schema(description = "판매채널 ID") long salesChannelId,
            @Schema(description = "샵 ID") long shopId,
            @Schema(description = "샵 코드") String shopCode,
            @Schema(description = "샵 이름") String shopName,
            @Schema(description = "외부몰 주문번호") String externalOrderNo,
            @Schema(description = "외부몰 주문일시 (KST)") String externalOrderedAt,
            @Schema(description = "구매자명") String buyerName,
            @Schema(description = "구매자 이메일") String buyerEmail,
            @Schema(description = "구매자 연락처") String buyerPhone,
            @Schema(description = "주문 생성일시 (KST)") String createdAt,
            @Schema(description = "주문 수정일시 (KST)") String updatedAt) {}

    /** 상품주문 정보. */
    @Schema(description = "상품주문 정보")
    public record ProductOrderApiResponse(
            @Schema(description = "상품주문 ID (UUIDv7)") String orderItemId,
            @Schema(description = "상품주문 번호 (ORD-YYYYMMDD-XXXX-NNN)") String orderItemNumber,
            @Schema(description = "상품그룹 ID") long productGroupId,
            @Schema(description = "상품 ID (SKU)") long productId,
            @Schema(description = "SKU 코드") String skuCode,
            @Schema(description = "상품명") String productGroupName,
            @Schema(description = "브랜드명 (스냅샷)") String brandName,
            @Schema(description = "판매자명 (스냅샷)") String sellerName,
            @Schema(description = "대표 이미지 URL") String mainImageUrl,
            @Schema(description = "외부 상품 ID") String externalProductId,
            @Schema(description = "외부 옵션 ID") String externalOptionId,
            @Schema(description = "외부 상품명") String externalProductName,
            @Schema(description = "외부 옵션명") String externalOptionName,
            @Schema(description = "외부 이미지 URL") String externalImageUrl,
            @Schema(description = "개당 판매가 (원)") int unitPrice,
            @Schema(description = "주문 수량") int quantity,
            @Schema(description = "총 금액") int totalAmount,
            @Schema(description = "할인 금액") int discountAmount,
            @Schema(description = "실결제 금액") int paymentAmount) {}

    /** 결제 정보. 리스트/상세 동일 구조. */
    @Schema(description = "결제 정보")
    public record PaymentInfoApiResponse(
            @Schema(description = "결제 ID (UUIDv7)") String paymentId,
            @Schema(description = "결제 번호 (PAY-YYYYMMDD-XXXX)") String paymentNumber,
            @Schema(description = "결제 상태") String paymentStatus,
            @Schema(description = "결제 수단") String paymentMethod,
            @Schema(description = "PG사 거래 ID") String paymentAgencyId,
            @Schema(description = "결제 금액 (원)") int paymentAmount,
            @Schema(description = "결제일시 (KST)") String paidAt,
            @Schema(description = "취소일시 (KST)") String canceledAt) {}

    /** 수령인/배송지 정보. */
    @Schema(description = "수령인/배송지 정보")
    public record ReceiverApiResponse(
            @Schema(description = "수령인명") String receiverName,
            @Schema(description = "수령인 연락처") String receiverPhone,
            @Schema(description = "우편번호") String receiverZipcode,
            @Schema(description = "기본 주소") String receiverAddress,
            @Schema(description = "상세 주소") String receiverAddressDetail,
            @Schema(description = "배송 요청사항") String deliveryRequest) {}

    /** 배송 상태. */
    @Schema(description = "배송 상태")
    public record DeliveryApiResponse(@Schema(description = "주문 상품 상태") String orderItemStatus) {}

    /** 취소 요약 (배송 전 취소). */
    @Schema(description = "취소 요약")
    public record CancelSummaryApiResponse(
            @Schema(description = "진행 중인 취소 존재 여부") boolean hasActiveCancel,
            @Schema(description = "총 취소 수량") int totalCancelledQty,
            @Schema(description = "추가 취소 가능 수량") int cancelableQty,
            @Schema(description = "가장 최근 취소 정보") LatestCancelApiResponse latest) {

        @Schema(description = "최근 취소 정보")
        public record LatestCancelApiResponse(
                @Schema(description = "취소 ID (UUIDv7)") String cancelId,
                @Schema(description = "취소 번호") String cancelNumber,
                @Schema(description = "취소 상태") String status,
                @Schema(description = "취소 수량") int qty,
                @Schema(description = "취소 신청일시 (KST)") String requestedAt) {}
    }

    /** 클레임 요약 (환불/교환, 배송 후). */
    @Schema(description = "클레임 요약")
    public record ClaimSummaryApiResponse(
            @Schema(description = "진행 중인 클레임 존재 여부") boolean hasActiveClaim,
            @Schema(description = "진행 중인 클레임 수") int activeCount,
            @Schema(description = "총 클레임 수량") int totalClaimedQty,
            @Schema(description = "추가 클레임 가능 수량") int claimableQty,
            @Schema(description = "가장 최근 클레임 정보") LatestClaimApiResponse latest) {

        @Schema(description = "최근 클레임 정보")
        public record LatestClaimApiResponse(
                @Schema(description = "클레임 ID (UUIDv7)") String claimId,
                @Schema(description = "클레임 번호") String claimNumber,
                @Schema(description = "클레임 유형 (REFUND, EXCHANGE)") String type,
                @Schema(description = "클레임 상태") String status,
                @Schema(description = "클레임 수량") int qty,
                @Schema(description = "클레임 신청일시 (KST)") String requestedAt) {}
    }
}
