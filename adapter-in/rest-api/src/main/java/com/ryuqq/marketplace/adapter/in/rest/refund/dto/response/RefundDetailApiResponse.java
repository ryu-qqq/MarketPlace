package com.ryuqq.marketplace.adapter.in.rest.refund.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 환불 상세 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외.
 */
@Schema(description = "환불 상세")
public record RefundDetailApiResponse(
        @Schema(description = "환불 클레임 ID (UUIDv7)") String refundClaimId,
        @Schema(description = "환불 클레임 번호") String claimNumber,
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "환불 수량") int refundQty,
        @Schema(description = "환불 상태") String refundStatus,
        @Schema(description = "환불 사유 유형") String reasonType,
        @Schema(description = "환불 상세 사유") String reasonDetail,
        @Schema(description = "환불 정보") RefundInfoApiResponse refundInfo,
        @Schema(description = "보류 정보") HoldInfoApiResponse holdInfo,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "요청일시") String requestedAt,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "완료일시") String completedAt,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt,
        @Schema(description = "클레임 이력 목록") List<ClaimHistoryApiResponse> claimHistories) {

    @Schema(description = "환불 금액 정보")
    public record RefundInfoApiResponse(
            @Schema(description = "원래 금액") int originalAmount,
            @Schema(description = "최종 환불 금액") int finalAmount,
            @Schema(description = "차감 금액") int deductionAmount,
            @Schema(description = "차감 사유") String deductionReason,
            @Schema(description = "환불 방식") String refundMethod,
            @Schema(description = "환불 완료일시") String refundedAt) {}

    @Schema(description = "보류 정보")
    public record HoldInfoApiResponse(
            @Schema(description = "보류 사유") String holdReason,
            @Schema(description = "보류 시각") String holdAt) {}
}
