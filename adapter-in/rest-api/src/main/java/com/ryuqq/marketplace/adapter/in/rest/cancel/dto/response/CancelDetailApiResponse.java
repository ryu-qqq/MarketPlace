package com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 취소 상세 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외.
 */
@Schema(description = "취소 상세")
public record CancelDetailApiResponse(
        @Schema(description = "취소 ID (UUIDv7)") String cancelId,
        @Schema(description = "취소 번호") String cancelNumber,
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "취소 수량") int cancelQty,
        @Schema(description = "취소 유형") String cancelType,
        @Schema(description = "취소 상태") String cancelStatus,
        @Schema(description = "취소 사유 유형") String reasonType,
        @Schema(description = "취소 상세 사유") String reasonDetail,
        @Schema(description = "환불 정보") RefundInfoApiResponse refundInfo,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "요청일시") String requestedAt,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "완료일시") String completedAt,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt,
        @Schema(description = "클레임 이력 목록") List<ClaimHistoryApiResponse> claimHistories,
        @Schema(description = "결제 정보") ClaimListItemApiResponseV4.PaymentV4 payment) {

    @Schema(description = "환불 정보")
    public record RefundInfoApiResponse(
            @Schema(description = "환불 금액") int refundAmount,
            @Schema(description = "환불 방식") String refundMethod,
            @Schema(description = "환불 상태") String refundStatus,
            @Schema(description = "환불 완료일시") String refundedAt,
            @Schema(description = "PG 환불 ID") String pgRefundId) {}
}
