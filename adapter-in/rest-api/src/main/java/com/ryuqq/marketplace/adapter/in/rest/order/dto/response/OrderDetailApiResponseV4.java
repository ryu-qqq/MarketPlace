package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 주문 상세 응답 (V4 스펙).
 *
 * <p>legacyOrderId 제외. orderItem 단위 데이터를 V4 OrderDetail 형태로 변환. 미존재 필드는 0/"" 기본값.
 */
@Schema(description = "주문 상세 (V4)")
@SuppressFBWarnings(
        value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
        justification = "record의 List 필드는 API 응답 DTO이므로 방어적 복사 불필요")
public record OrderDetailApiResponseV4(
        @Schema(description = "주문 ID (UUIDv7)") String orderId,
        @Schema(description = "주문번호 (ORD-YYYYMMDD-XXXX)") String orderNumber,
        @Schema(description = "구매자 정보") OrderListApiResponseV4.BuyerInfoApiResponse buyerInfo,
        @Schema(description = "결제 정보") OrderListApiResponseV4.PaymentDetailApiResponse payment,
        @Schema(description = "수령인 정보") OrderListApiResponseV4.ReceiverInfoApiResponse receiverInfo,
        @Schema(description = "배송 정보")
                OrderListApiResponseV4.PaymentShipmentInfoApiResponse paymentShipmentInfo,
        @Schema(description = "주문 상품 정보")
                OrderListApiResponseV4.OrderProductApiResponse orderProduct,
        @Schema(description = "외부몰 주문 정보 (자사몰이면 null)")
                OrderListApiResponseV4.ExternalOrderInfoApiResponse externalOrderInfo,
        @Schema(description = "취소 요약 (없으면 null)")
                OrderListApiResponseV4.CancelSummaryV4ApiResponse cancel,
        @Schema(description = "클레임 요약 (없으면 null)")
                OrderListApiResponseV4.ClaimSummaryV4ApiResponse claim,
        @Schema(description = "주문 상태 변경 이력") List<OrderHistoryItemApiResponse> orderHistories,
        @Schema(description = "취소 ID 목록") List<String> cancelIds,
        @Schema(description = "취소 상세 목록 (최근 3개)") List<CancelItemApiResponse> cancels,
        @Schema(description = "클레임 ID 목록") List<String> claimIds,
        @Schema(description = "클레임 상세 목록 (최근 3개)") List<ClaimItemApiResponse> claims) {

    @Schema(description = "주문 상태 변경 이력 (V4 OrderHistoryItem)")
    public record OrderHistoryItemApiResponse(
            @Schema(description = "주문 ID") String orderId,
            @Schema(description = "상태 변경 사유") String changeReason,
            @Schema(description = "상세 변경 사유") String changeDetailReason,
            @Schema(description = "변경된 주문 상태") String orderStatus,
            @Schema(description = "이력 생성 일시") String createdAt,
            @Schema(description = "이력 수정 일시") String updatedAt) {}

    @Schema(description = "취소 상세 (V4 CancelItem)")
    public record CancelItemApiResponse(
            @Schema(description = "취소 ID (UUIDv7)") String cancelId,
            @Schema(description = "취소 번호") String cancelNumber,
            @Schema(description = "취소 유형 (DB 없음 시 \"\")") String type,
            @Schema(description = "취소 상태") String status,
            @Schema(description = "취소 수량") int qty,
            @Schema(description = "취소 사유") ReasonApiResponse reason,
            @Schema(description = "환불 정보") CancelRefundInfoApiResponse refundInfo,
            @Schema(description = "취소 신청 일시") String requestedAt,
            @Schema(description = "완료 일시") String completedAt,
            @Schema(description = "생성 일시") String createdAt) {

        @Schema(description = "사유")
        public record ReasonApiResponse(
                @Schema(description = "사유 유형") String reasonType,
                @Schema(description = "상세 사유") String reasonDetail) {}

        @Schema(description = "취소 환불 정보")
        public record CancelRefundInfoApiResponse(
                @Schema(description = "원 상품 금액") int originalAmount,
                @Schema(description = "최종 환불 금액") int finalAmount,
                @Schema(description = "환불 방식") String refundMethod,
                @Schema(description = "환불 완료 일시") String refundedAt) {}
    }

    @Schema(description = "클레임 상세 (V4 ClaimItem)")
    public record ClaimItemApiResponse(
            @Schema(description = "클레임 ID (UUIDv7)") String claimId,
            @Schema(description = "클레임 번호") String claimNumber,
            @Schema(description = "클레임 유형 (REFUND, EXCHANGE)") String type,
            @Schema(description = "클레임 상태") String status,
            @Schema(description = "클레임 수량") int qty,
            @Schema(description = "클레임 사유") ClaimReasonApiResponse reason,
            @Schema(description = "회수 방식") String collectMethod,
            @Schema(description = "환불 정보 (REFUND 시)") ClaimRefundInfoApiResponse refundInfo,
            @Schema(description = "교환 정보 (EXCHANGE 시, order_exchanges JOIN 전 null)")
                    ClaimExchangeInfoApiResponse exchangeInfo,
            @Schema(description = "클레임 신청 일시") String requestedAt,
            @Schema(description = "클레임 완료 일시") String completedAt,
            @Schema(description = "클레임 거절 일시") String rejectedAt,
            @Schema(description = "생성 일시") String createdAt) {

        @Schema(description = "클레임 사유")
        public record ClaimReasonApiResponse(
                @Schema(description = "사유 유형") String reasonType,
                @Schema(description = "상세 사유") String reasonDetail) {}

        @Schema(description = "클레임 환불 정보")
        public record ClaimRefundInfoApiResponse(
                @Schema(description = "원 상품 금액") int originalAmount,
                @Schema(description = "차감 금액") int deductionAmount,
                @Schema(description = "차감 사유") String deductionReason,
                @Schema(description = "최종 환불 금액") int finalAmount,
                @Schema(description = "환불 방식") String refundMethod,
                @Schema(description = "환불 완료 일시") String refundedAt) {}

        @Schema(description = "클레임 교환 정보 (order_exchanges JOIN 필요)")
        public record ClaimExchangeInfoApiResponse(
                @Schema(description = "교환 상품 그룹 ID") long newProductGroupId,
                @Schema(description = "교환 상품 ID") long newProductId,
                @Schema(description = "교환 옵션명") String newOptionName,
                @Schema(description = "가격 차이") int priceDifference,
                @Schema(description = "신규 주문 ID") String newOrderId,
                @Schema(description = "신규 주문번호") String newOrderNumber) {}
    }
}
