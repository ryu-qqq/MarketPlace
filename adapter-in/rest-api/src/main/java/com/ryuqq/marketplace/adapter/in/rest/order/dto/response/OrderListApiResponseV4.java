package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 주문 리스트 항목 응답 (V4 스펙).
 *
 * <p>legacyOrderId, settlementInfo 제외. V5 ProductOrderListResult에서 변환 시 미존재 필드는 0/"" 기본값 적용.
 */
@Schema(description = "주문 리스트 항목 (V4)")
public record OrderListApiResponseV4(
        @Schema(description = "주문 ID (UUIDv7)") String orderId,
        @Schema(description = "주문번호 (ORD-YYYYMMDD-XXXX)") String orderNumber,
        @Schema(description = "구매자 정보") BuyerInfoApiResponse buyerInfo,
        @Schema(description = "결제 정보") PaymentDetailApiResponse payment,
        @Schema(description = "수령인 정보") ReceiverInfoApiResponse receiverInfo,
        @Schema(description = "배송 정보") PaymentShipmentInfoApiResponse paymentShipmentInfo,
        @Schema(description = "주문 상품 정보") OrderProductApiResponse orderProduct,
        @Schema(description = "외부몰 주문 정보 (자사몰이면 null)") ExternalOrderInfoApiResponse externalOrderInfo,
        @Schema(description = "취소 요약 (없으면 null)") CancelSummaryV4ApiResponse cancel,
        @Schema(description = "클레임 요약 (없으면 null)") ClaimSummaryV4ApiResponse claim) {

    @Schema(description = "구매자 정보")
    public record BuyerInfoApiResponse(
            @Schema(description = "구매자 이름") String buyerName,
            @Schema(description = "구매자 이메일") String buyerEmail,
            @Schema(description = "구매자 연락처") String buyerPhoneNumber) {}

    @Schema(description = "결제 정보 (V4 PaymentDetail)")
    public record PaymentDetailApiResponse(
            @Schema(description = "결제 ID") long paymentId,
            @Schema(description = "PG사 거래 ID") String paymentAgencyId,
            @Schema(description = "결제 상태") String paymentStatus,
            @Schema(description = "결제 수단") String paymentMethod,
            @Schema(description = "결제 일시 (yyyy-MM-dd HH:mm:ss)") String paymentDate,
            @Schema(description = "취소 일시") String canceledDate,
            @Schema(description = "사용자 ID (구매자)") long userId,
            @Schema(description = "사이트 (판매 채널)") String siteName,
            @Schema(description = "청구 금액 (원)") int billAmount,
            @Schema(description = "실결제 금액 (원)") int paymentAmount,
            @Schema(description = "사용 마일리지 (원)") int usedMileageAmount) {}

    @Schema(description = "수령인 정보")
    public record ReceiverInfoApiResponse(
            @Schema(description = "수령인 이름") String receiverName,
            @Schema(description = "수령인 연락처") String receiverPhoneNumber,
            @Schema(description = "기본 주소 (도로명/지번)") String addressLine1,
            @Schema(description = "상세 주소 (동/호수)") String addressLine2,
            @Schema(description = "우편번호") String zipCode,
            @Schema(description = "배송 요청사항") String deliveryRequest) {}

    @Schema(description = "배송 정보")
    public record PaymentShipmentInfoApiResponse(
            @Schema(description = "배송 상태") String deliveryStatus,
            @Schema(description = "택배사 코드") String shipmentCompanyCode,
            @Schema(description = "송장번호") String invoice,
            @Schema(description = "출고 완료 일시 (yyyy-MM-dd HH:mm:ss)") String shipmentCompletedDate) {}

    @Schema(description = "가격 정보")
    public record PriceApiResponse(
            @Schema(description = "정가 (원)") int regularPrice,
            @Schema(description = "현재가 (원)") int currentPrice,
            @Schema(description = "판매가 (원)") int salePrice,
            @Schema(description = "직접 할인가 (원)") int directDiscountPrice,
            @Schema(description = "직접 할인율 (%)") int directDiscountRate,
            @Schema(description = "할인율 (%)") int discountRate) {}

    @Schema(description = "브랜드 정보")
    public record BrandApiResponse(
            @Schema(description = "브랜드 ID") long brandId,
            @Schema(description = "브랜드명") String brandName) {}

    @Schema(description = "옵션 상세")
    public record OptionDtoApiResponse(
            @Schema(description = "옵션명") String optionName,
            @Schema(description = "옵션값") String optionValue) {}

    @Schema(description = "주문 상품 정보 (V4 OrderProduct)")
    public record OrderProductApiResponse(
            @Schema(description = "주문 ID") String orderId,
            @Schema(description = "상품 그룹명") String productGroupName,
            @Schema(description = "가격 정보") PriceApiResponse price,
            @Schema(description = "브랜드 정보") BrandApiResponse brand,
            @Schema(description = "상품 그룹 ID") long productGroupId,
            @Schema(description = "상품 ID (SKU)") long productId,
            @Schema(description = "판매자명") String sellerName,
            @Schema(description = "상품 대표 이미지 URL") String productGroupMainImageUrl,
            @Schema(description = "배송 지역") String deliveryArea,
            @Schema(description = "주문 수량") int productQuantity,
            @Schema(description = "주문 상태") String orderStatus,
            @Schema(description = "정가 (원)") int regularPrice,
            @Schema(description = "주문 금액 (원)") int orderAmount,
            @Schema(description = "예상 환불 마일리지 (원)") int totalExpectedRefundMileageAmount,
            @Schema(description = "옵션 문자열") String option,
            @Schema(description = "SKU 번호") String skuNumber,
            @Schema(description = "옵션 상세 목록") java.util.List<OptionDtoApiResponse> options) {}

    @Schema(description = "외부몰 주문 정보")
    public record ExternalOrderInfoApiResponse(
            @Schema(description = "샵 ID") long shopId,
            @Schema(description = "샵 코드") String shopCode,
            @Schema(description = "외부몰 주문번호") String shopOrderNo,
            @Schema(description = "외부몰 주문상태") String shopOrderStatus,
            @Schema(description = "외부몰 주문일시") String shopOrderedAt) {}

    @Schema(description = "취소 요약 (V4)")
    public record CancelSummaryV4ApiResponse(
            @Schema(description = "진행 중인 취소 존재 여부") boolean hasActiveCancel,
            @Schema(description = "총 취소 수량") int totalCancelledQty,
            @Schema(description = "추가 취소 가능 수량") int cancelableQty,
            @Schema(description = "가장 최근 취소 정보") LatestCancelV4ApiResponse latest) {

        @Schema(description = "최근 취소 정보")
        public record LatestCancelV4ApiResponse(
                @Schema(description = "취소 ID (UUIDv7)") String cancelId,
                @Schema(description = "취소 번호") String cancelNumber,
                @Schema(description = "취소 유형") String type,
                @Schema(description = "취소 상태") String status,
                @Schema(description = "취소 수량") int qty,
                @Schema(description = "취소 신청 일시") String requestedAt) {}
    }

    @Schema(description = "클레임 요약 (V4)")
    public record ClaimSummaryV4ApiResponse(
            @Schema(description = "진행 중인 클레임 존재 여부") boolean hasActiveClaim,
            @Schema(description = "진행 중인 클레임 수") int activeCount,
            @Schema(description = "총 클레임 수량") int totalClaimedQty,
            @Schema(description = "추가 클레임 가능 수량") int claimableQty,
            @Schema(description = "가장 최근 클레임 정보") LatestClaimV4ApiResponse latest) {

        @Schema(description = "최근 클레임 정보")
        public record LatestClaimV4ApiResponse(
                @Schema(description = "클레임 ID (UUIDv7)") String claimId,
                @Schema(description = "클레임 번호") String claimNumber,
                @Schema(description = "클레임 유형 (REFUND, EXCHANGE)") String type,
                @Schema(description = "클레임 상태") String status,
                @Schema(description = "클레임 수량") int qty,
                @Schema(description = "클레임 신청 일시") String requestedAt) {}
    }
}
