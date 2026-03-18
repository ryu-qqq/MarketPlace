package com.ryuqq.marketplace.adapter.in.rest.refund.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 환불 목록 항목 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외.
 */
@Schema(description = "환불 목록 항목")
public record RefundListApiResponse(
        @Schema(description = "환불 클레임 ID (UUIDv7)") String refundClaimId,
        @Schema(description = "환불 클레임 번호") String claimNumber,
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "환불 수량") int refundQty,
        @Schema(description = "환불 상태") String refundStatus,
        @Schema(description = "환불 사유 유형") String reasonType,
        @Schema(description = "환불 상세 사유") String reasonDetail,
        @Schema(description = "원래 금액") int originalAmount,
        @Schema(description = "최종 환불 금액") int finalAmount,
        @Schema(description = "환불 방식") String refundMethod,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "요청일시") String requestedAt,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "완료일시") String completedAt) {}
