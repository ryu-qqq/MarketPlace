package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ClaimSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.DeliveryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.OrderInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.PaymentInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ProductOrderApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ReceiverApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 상품주문 상세 조회 응답 (V5).
 *
 * <p>리스트와 동일한 객체 구조 + settlement, cancels, claims, timeLine 추가.
 */
@Schema(description = "상품주문 상세 정보")
public record OrderDetailApiResponse(
        @Schema(description = "소속 주문 정보") OrderInfoApiResponse order,
        @Schema(description = "상품주문 정보") ProductOrderApiResponse productOrder,
        @Schema(description = "결제 정보") PaymentInfoApiResponse payment,
        @Schema(description = "수령인/배송지 정보") ReceiverApiResponse receiver,
        @Schema(description = "배송 상태") DeliveryApiResponse delivery,
        @Schema(description = "취소 요약 (없으면 null)") CancelSummaryApiResponse cancel,
        @Schema(description = "클레임 요약 (없으면 null)") ClaimSummaryApiResponse claim,
        @Schema(description = "정산 정보") SettlementApiResponse settlement,
        @Schema(description = "취소 상세 목록") List<CancelInfoApiResponse> cancels,
        @Schema(description = "클레임 상세 목록") List<ClaimInfoApiResponse> claims,
        @Schema(description = "주문 타임라인") List<TimeLineApiResponse> timeLine) {

    /** 정산 정보. */
    @Schema(description = "정산 정보")
    public record SettlementApiResponse(
            @Schema(description = "수수료율 (%)") double commissionRate,
            @Schema(description = "수수료 금액") int fee,
            @Schema(description = "예상 정산 금액") int expectationSettlementAmount,
            @Schema(description = "정산 금액") int settlementAmount,
            @Schema(description = "쉐어 비율 (%)") double shareRatio,
            @Schema(description = "정산 예정일") String expectedSettlementDay,
            @Schema(description = "정산 완료일") String settlementDay) {}

    /** 취소 상세. */
    @Schema(description = "취소 상세 정보")
    public record CancelInfoApiResponse(
            @Schema(description = "취소 ID (UUIDv7)") String cancelId,
            @Schema(description = "취소 대상 상품주문 ID (UUIDv7)") String orderItemId,
            @Schema(description = "취소번호") String cancelNumber,
            @Schema(description = "취소 상태") String cancelStatus,
            @Schema(description = "취소 수량") int quantity,
            @Schema(description = "취소 사유 유형") String reasonType,
            @Schema(description = "취소 상세 사유") String reasonDetail,
            @Schema(description = "원 금액") int originalAmount,
            @Schema(description = "환불 금액") int refundAmount,
            @Schema(description = "환불 수단") String refundMethod,
            @Schema(description = "환불일시 (ISO 8601)") String refundedAt,
            @Schema(description = "취소 요청일시 (ISO 8601)") String requestedAt,
            @Schema(description = "취소 완료일시 (ISO 8601)") String completedAt) {}

    /** 클레임 상세. */
    @Schema(description = "클레임 상세 정보")
    public record ClaimInfoApiResponse(
            @Schema(description = "클레임 ID (UUIDv7)") String claimId,
            @Schema(description = "클레임 대상 상품주문 ID (UUIDv7)") String orderItemId,
            @Schema(description = "클레임번호") String claimNumber,
            @Schema(description = "클레임 유형 (REFUND, EXCHANGE)") String claimType,
            @Schema(description = "클레임 상태") String claimStatus,
            @Schema(description = "클레임 수량") int quantity,
            @Schema(description = "클레임 사유 유형") String reasonType,
            @Schema(description = "클레임 상세 사유") String reasonDetail,
            @Schema(description = "회수 방식") String collectMethod,
            @Schema(description = "원 금액") int originalAmount,
            @Schema(description = "차감 금액") int deductionAmount,
            @Schema(description = "차감 사유") String deductionReason,
            @Schema(description = "환불 금액") int refundAmount,
            @Schema(description = "환불 수단") String refundMethod,
            @Schema(description = "환불일시 (ISO 8601)") String refundedAt,
            @Schema(description = "클레임 요청일시 (ISO 8601)") String requestedAt,
            @Schema(description = "클레임 완료일시 (ISO 8601)") String completedAt,
            @Schema(description = "클레임 거절일시 (ISO 8601)") String rejectedAt) {}

    /** 주문 타임라인 항목. */
    @Schema(description = "타임라인 항목")
    public record TimeLineApiResponse(
            @Schema(description = "이력 ID") long historyId,
            @Schema(description = "이전 상태") String fromStatus,
            @Schema(description = "변경된 상태") String toStatus,
            @Schema(description = "변경자") String changedBy,
            @Schema(description = "변경 사유") String reason,
            @Schema(description = "변경일시 (ISO 8601)") String changedAt) {}
}
