package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 교환 상세 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외. 취소 상세와 동일하게 orderProduct, buyerInfo, payment,
 * receiverInfo를 포함한다.
 */
@Schema(description = "교환 상세")
public record ExchangeDetailApiResponse(
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "주문 상품 정보") ClaimListItemApiResponseV4.OrderProductV4 orderProduct,
        @Schema(description = "교환 클레임 정보") ExchangeClaimInfoApiResponse exchangeClaimInfo,
        @Schema(description = "구매자 정보") ClaimListItemApiResponseV4.BuyerInfoV4 buyerInfo,
        @Schema(description = "결제 정보") ClaimListItemApiResponseV4.PaymentV4 payment,
        @Schema(description = "수령인 정보") ClaimListItemApiResponseV4.ReceiverInfoV4 receiverInfo,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt,
        @Schema(description = "클레임 이력 목록") List<ClaimHistoryApiResponse> claimHistories) {

    @Schema(description = "교환 클레임 정보")
    public record ExchangeClaimInfoApiResponse(
            @Schema(description = "교환 클레임 ID (UUIDv7)") String exchangeClaimId,
            @Schema(description = "교환 클레임 번호") String claimNumber,
            @Schema(description = "판매자 ID") long sellerId,
            @Schema(description = "교환 수량") int exchangeQty,
            @Schema(description = "교환 상태") String exchangeStatus,
            @Schema(description = "교환 사유 유형") String reasonType,
            @Schema(description = "교환 상세 사유") String reasonDetail,
            @Schema(description = "교환 옵션 정보") ExchangeOptionApiResponse exchangeOption,
            @Schema(description = "금액 조정 정보") AmountAdjustmentApiResponse amountAdjustment,
            @Schema(description = "수거 배송 정보") CollectShipmentApiResponse collectShipment,
            @Schema(description = "연결 주문 ID") String linkedOrderId,
            @Schema(description = "요청일시") String requestedAt,
            @Schema(description = "완료일시") String completedAt) {}

    @Schema(description = "교환 옵션 정보")
    public record ExchangeOptionApiResponse(
            @Schema(description = "원 상품 ID") long originalProductId,
            @Schema(description = "원 SKU 코드") String originalSkuCode,
            @Schema(description = "교환 대상 상품 그룹 ID") long targetProductGroupId,
            @Schema(description = "교환 대상 상품 ID") long targetProductId,
            @Schema(description = "교환 대상 SKU 코드") String targetSkuCode,
            @Schema(description = "수량") int quantity) {}

    @Schema(description = "금액 조정 정보")
    public record AmountAdjustmentApiResponse(
            @Schema(description = "원 가격") int originalPrice,
            @Schema(description = "교환 대상 가격") int targetPrice,
            @Schema(description = "가격 차액") int priceDifference,
            @Schema(description = "추가 결제 필요 여부") boolean additionalPaymentRequired,
            @Schema(description = "부분 환불 필요 여부") boolean partialRefundRequired,
            @Schema(description = "수거 배송비") int collectShippingFee,
            @Schema(description = "재발송 배송비") int reshipShippingFee,
            @Schema(description = "총 배송비") int totalShippingFee,
            @Schema(description = "배송비 부담자") String shippingFeePayer) {}

    @Schema(description = "수거 배송 정보")
    public record CollectShipmentApiResponse(
            @Schema(description = "수거 택배사명") String collectDeliveryCompany,
            @Schema(description = "수거 송장번호") String collectTrackingNumber,
            @Schema(description = "수거 상태") String collectStatus) {}
}
