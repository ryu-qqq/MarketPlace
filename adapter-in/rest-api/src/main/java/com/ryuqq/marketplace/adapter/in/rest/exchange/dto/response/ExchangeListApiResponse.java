package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 교환 목록 항목 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외.
 */
@Schema(description = "교환 목록 항목")
public record ExchangeListApiResponse(
        @Schema(description = "교환 클레임 ID (UUIDv7)") String exchangeClaimId,
        @Schema(description = "교환 클레임 번호") String claimNumber,
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "교환 수량") int exchangeQty,
        @Schema(description = "교환 상태") String exchangeStatus,
        @Schema(description = "교환 사유 유형") String reasonType,
        @Schema(description = "교환 상세 사유") String reasonDetail,
        @Schema(description = "교환 대상 SKU 코드") String targetSkuCode,
        @Schema(description = "교환 대상 수량") int targetQuantity,
        @Schema(description = "연결 주문 ID") String linkedOrderId,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "요청일시") String requestedAt,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "완료일시") String completedAt) {}
