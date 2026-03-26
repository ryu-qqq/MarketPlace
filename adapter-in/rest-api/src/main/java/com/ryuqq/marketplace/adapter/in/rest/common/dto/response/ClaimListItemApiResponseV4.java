package com.ryuqq.marketplace.adapter.in.rest.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 클레임(반품/취소/교환) 리스트 공통 응답 (V4 스펙).
 *
 * <p>프론트엔드 OMS 테이블 컬럼 구조에 맞춘 중첩 응답. OrderListApiResponseV4의 중첩 타입을 재사용하지 않고 독립적으로 정의하여 클레임 도메인과
 * 결합도를 낮춘다.
 */
@Schema(description = "클레임 리스트 항목 (V4)")
public record ClaimListItemApiResponseV4(
        @Schema(description = "주문 상품 정보") OrderProductV4 orderProduct,
        @Schema(description = "클레임 정보") ClaimInfoV4 claimInfo,
        @Schema(description = "구매자 정보") BuyerInfoV4 buyerInfo,
        @Schema(description = "결제 정보") PaymentV4 payment,
        @Schema(description = "수령인 정보") ReceiverInfoV4 receiverInfo,
        @Schema(description = "외부몰 주문 정보") ExternalOrderInfoV4 externalOrderInfo) {

    @Schema(description = "주문 상품 정보 (V4)")
    public record OrderProductV4(
            @Schema(description = "주문 ID (프론트: orderId = orderItemId)") String orderId,
            @Schema(description = "주문번호") String orderNumber,
            @Schema(description = "상품 그룹명") String productGroupName,
            @Schema(description = "가격 정보") PriceV4 price,
            @Schema(description = "브랜드 정보") BrandV4 brand,
            @Schema(description = "상품 그룹 ID") long productGroupId,
            @Schema(description = "상품 ID") long productId,
            @Schema(description = "판매자명") String sellerName,
            @Schema(description = "상품 대표 이미지 URL") String productGroupMainImageUrl,
            @Schema(description = "배송 지역") String deliveryArea,
            @Schema(description = "주문 수량") int productQuantity,
            @Schema(description = "주문 상태") String orderStatus,
            @Schema(description = "정가") int regularPrice,
            @Schema(description = "주문 금액") int orderAmount,
            @Schema(description = "예상 환불 마일리지") int totalExpectedRefundMileageAmount,
            @Schema(description = "옵션 문자열") String option,
            @Schema(description = "SKU 번호") String skuNumber,
            @Schema(description = "옵션 상세 목록") List<OptionV4> options) {
        public OrderProductV4 {
            options = options != null ? List.copyOf(options) : List.of();
        }
    }

    @Schema(description = "가격 정보 (V4)")
    public record PriceV4(
            @Schema(description = "정가") int regularPrice,
            @Schema(description = "현재가") int currentPrice,
            @Schema(description = "판매가") int salePrice,
            @Schema(description = "직접 할인가") int directDiscountPrice,
            @Schema(description = "직접 할인율") int directDiscountRate,
            @Schema(description = "할인율") int discountRate) {}

    @Schema(description = "브랜드 정보 (V4)")
    public record BrandV4(
            @Schema(description = "브랜드 ID") long brandId,
            @Schema(description = "브랜드명") String brandName) {}

    @Schema(description = "옵션 상세 (V4)")
    public record OptionV4(
            @Schema(description = "옵션명") String optionName,
            @Schema(description = "옵션값") String optionValue) {}

    @Schema(description = "클레임 정보 (V4)")
    public record ClaimInfoV4(
            @Schema(description = "클레임 ID") String claimId,
            @Schema(description = "클레임 번호") String claimNumber,
            @Schema(description = "클레임 상태") String status,
            @Schema(description = "클레임 수량") int qty,
            @Schema(description = "클레임 사유") String reason,
            @Schema(description = "환불 정보") RefundInfoV4 refundInfo,
            @Schema(description = "수거 배송 정보") CollectShipmentV4 collectShipment,
            @Schema(description = "보류 사유") String holdReason,
            @Schema(description = "보류 여부") boolean isHold,
            @Schema(description = "요청 일시") String requestedAt,
            @Schema(description = "생성 일시") String createdAt,
            @Schema(description = "교환 옵션 정보 (교환 전용, 반품/취소 시 null)") ExchangeOptionV4 exchangeOption) {}

    @Schema(description = "교환 옵션 정보 (V4)")
    public record ExchangeOptionV4(
            @Schema(description = "교환 전 옵션") OptionInfoV4 originalOption,
            @Schema(description = "교환 후 옵션") OptionInfoV4 targetOption) {}

    @Schema(description = "옵션 정보 (V4)")
    public record OptionInfoV4(
            @Schema(description = "옵션명") String optionName,
            @Schema(description = "옵션 값 목록") List<OptionValueV4> optionValues) {
        public OptionInfoV4 {
            optionValues = optionValues != null ? List.copyOf(optionValues) : List.of();
        }
    }

    @Schema(description = "옵션 값 (V4)")
    public record OptionValueV4(
            @Schema(description = "옵션 항목명") String name,
            @Schema(description = "옵션 항목 값") String value) {}

    @Schema(description = "클레임 사유 (V4)")
    public record ReasonV4(
            @Schema(description = "사유 유형") String reasonType,
            @Schema(description = "상세 사유") String reasonDetail) {}

    @Schema(description = "환불 정보 (V4)")
    public record RefundInfoV4(
            @Schema(description = "원 금액") int originalAmount,
            @Schema(description = "차감 금액") int deductionAmount,
            @Schema(description = "차감 사유") String deductionReason,
            @Schema(description = "최종 환불 금액") int finalAmount,
            @Schema(description = "환불 방식") String refundMethod,
            @Schema(description = "환불 완료 일시") String refundedAt) {}

    @Schema(description = "수거 배송 정보 (V4)")
    public record CollectShipmentV4(
            @Schema(description = "수거 방법") MethodV4 method,
            @Schema(description = "송장번호") String trackingNumber,
            @Schema(description = "배송비 정보") FeeInfoV4 feeInfo,
            @Schema(description = "수거 완료 일시") String receivedAt) {}

    @Schema(description = "수거 방법 (V4)")
    public record MethodV4(
            @Schema(description = "수거 방법 유형 (COURIER, VISIT, QUICK)") String type,
            @Schema(description = "택배사명") String courierName) {}

    @Schema(description = "배송비 정보 (V4)")
    public record FeeInfoV4(
            @Schema(description = "배송비 부담 주체 (BUYER, SELLER)") String payer,
            @Schema(description = "배송비 금액") int amount) {}

    @Schema(description = "구매자 정보 (V4)")
    public record BuyerInfoV4(
            @Schema(description = "구매자명") String buyerName,
            @Schema(description = "구매자 연락처") String buyerPhoneNumber) {}

    @Schema(description = "결제 정보 (V4)")
    public record PaymentV4(
            @Schema(description = "결제 번호") String paymentNumber,
            @Schema(description = "결제 일시") String paymentDate,
            @Schema(description = "결제 금액") int paymentAmount,
            @Schema(description = "결제 수단") String paymentMethod) {}

    @Schema(description = "수령인 정보 (V4)")
    public record ReceiverInfoV4(
            @Schema(description = "수령인명") String receiverName,
            @Schema(description = "수령인 연락처") String receiverPhoneNumber,
            @Schema(description = "주소") String address,
            @Schema(description = "상세 주소") String addressDetail,
            @Schema(description = "우편번호") String zipCode) {}

    @Schema(description = "외부몰 주문 정보 (V4)")
    public record ExternalOrderInfoV4(
            @Schema(description = "샵 코드") String shopCode,
            @Schema(description = "외부몰 주문번호") String shopOrderNo) {}
}
