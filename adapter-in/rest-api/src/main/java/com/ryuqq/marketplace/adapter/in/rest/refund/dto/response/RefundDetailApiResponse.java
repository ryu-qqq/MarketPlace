package com.ryuqq.marketplace.adapter.in.rest.refund.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 환불 상세 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외. 취소 상세와 동일하게 orderProduct, buyerInfo,
 * payment, receiverInfo를 포함한다.
 */
@Schema(description = "환불 상세")
public record RefundDetailApiResponse(
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "주문 상품 정보") ClaimListItemApiResponseV4.OrderProductV4 orderProduct,
        @Schema(description = "환불 클레임 정보") RefundClaimInfoApiResponse refundClaimInfo,
        @Schema(description = "구매자 정보") ClaimListItemApiResponseV4.BuyerInfoV4 buyerInfo,
        @Schema(description = "결제 정보") ClaimListItemApiResponseV4.PaymentV4 payment,
        @Schema(description = "수령인 정보") ClaimListItemApiResponseV4.ReceiverInfoV4 receiverInfo,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt,
        @Schema(description = "클레임 이력 목록") List<ClaimHistoryApiResponse> claimHistories) {

    @Schema(description = "환불 클레임 정보")
    public record RefundClaimInfoApiResponse(
            @Schema(description = "환불 클레임 ID (UUIDv7)") String refundClaimId,
            @Schema(description = "환불 클레임 번호") String claimNumber,
            @Schema(description = "환불 수량") int refundQty,
            @Schema(description = "환불 상태") String refundStatus,
            @Schema(description = "환불 사유 유형") String reasonType,
            @Schema(description = "환불 상세 사유") String reasonDetail,
            @Schema(description = "환불 정보") ClaimListItemApiResponseV4.RefundInfoV4 refundInfo,
            @Schema(description = "보류 정보") HoldInfoApiResponse holdInfo,
            @Schema(description = "수거 배송 정보") CollectShipmentApiResponse collectShipment,
            @Schema(description = "요청일시") String requestedAt,
            @Schema(description = "완료일시") String completedAt) {}

    @Schema(description = "보류 정보")
    public record HoldInfoApiResponse(
            @Schema(description = "보류 사유") String holdReason,
            @Schema(description = "보류 시각") String holdAt) {}

    @Schema(description = "수거 배송 정보")
    public record CollectShipmentApiResponse(
            @Schema(description = "수거 택배사명") String collectDeliveryCompany,
            @Schema(description = "수거 송장번호") String collectTrackingNumber,
            @Schema(description = "수거 상태") String collectStatus) {}
}
