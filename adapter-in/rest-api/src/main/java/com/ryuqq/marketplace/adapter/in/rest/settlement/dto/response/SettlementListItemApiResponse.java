package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 정산 건별 목록 항목 응답 DTO (V4 스펙).
 *
 * <p>SETTLEMENT_SPEC_V1 의 SettlementListItem 구조를 그대로 반영한다. ClaimOrderEnricher 를 통해 주문 데이터를 배치
 * 보강하여 orderProduct, buyer, seller, payment 중첩 필드를 채운다.
 *
 * <p>V4 간극 규칙: API orderId = 내부 orderItemId.
 */
@Schema(description = "정산 건별 목록 항목 (V4)")
public record SettlementListItemApiResponse(
        @Schema(description = "정산 원장 ID") String settlementId,
        @Schema(description = "정산 상태 (PENDING, HOLD, COMPLETED)") String status,
        @Schema(description = "주문 ID (내부 orderItemId → API orderId 매핑)") String orderId,
        @Schema(description = "주문 번호") String orderNumber,
        @Schema(description = "주문 상품 정보") OrderProductV4 orderProduct,
        @Schema(description = "구매자 정보") BuyerInfoV4 buyer,
        @Schema(description = "판매자 정보") SellerInfoV4 seller,
        @Schema(description = "결제 정보") PaymentInfoV4 payment,
        @Schema(description = "금액 정보") SettlementAmountsV4 amounts,
        @Schema(description = "주문 일시") String orderedAt,
        @Schema(description = "배송 완료 일시") String deliveredAt,
        @Schema(description = "정산 예정일 (YYYY-MM-DD)") String expectedSettlementDay,
        @Schema(description = "정산 완료일 (YYYY-MM-DD)") String settlementDay,
        @Schema(description = "보류 정보 (보류 상태일 때만 존재)") HoldInfoV4 holdInfo) {

    /** 주문 상품 정보. */
    @Schema(description = "주문 상품 정보 (V4)")
    public record OrderProductV4(
            @Schema(description = "주문 ID") String orderId,
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

    /** 구매자 정보. */
    @Schema(description = "구매자 정보 (V4)")
    public record BuyerInfoV4(
            @Schema(description = "구매자명") String buyerName,
            @Schema(description = "구매자 연락처") String buyerPhoneNumber) {}

    /** 판매자 정보. */
    @Schema(description = "판매자 정보 (V4)")
    public record SellerInfoV4(
            @Schema(description = "판매자 ID") long sellerId,
            @Schema(description = "판매자명") String sellerName) {}

    /** 결제 정보. */
    @Schema(description = "결제 정보 (V4)")
    public record PaymentInfoV4(
            @Schema(description = "결제 번호") String paymentNumber,
            @Schema(description = "결제 일시") String paymentDate,
            @Schema(description = "결제 금액") int paymentAmount,
            @Schema(description = "결제 수단") String paymentMethod) {}

    /** 정산 금액 정보. */
    @Schema(description = "정산 금액 정보 (V4)")
    public record SettlementAmountsV4(
            @Schema(description = "판매 금액") int salesAmount,
            @Schema(description = "수수료 금액") int feeAmount,
            @Schema(description = "수수료율 (basis point, 1%=100)") int feeRate,
            @Schema(description = "정산 예정 금액") int expectedSettlementAmount,
            @Schema(description = "정산 금액") int settlementAmount) {}

    /** 보류 정보. */
    @Schema(description = "보류 정보 (V4)")
    public record HoldInfoV4(
            @Schema(description = "보류 사유") String holdReason,
            @Schema(description = "보류 처리 일시") String holdAt) {}
}
