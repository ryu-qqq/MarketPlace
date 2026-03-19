package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 정산 원장 목록 항목 응답 DTO.
 *
 * <p>V4 간극 규칙: API orderId = 내부 orderItemId.
 */
@Schema(description = "정산 원장 목록 항목")
public record SettlementListItemApiResponse(
        @Schema(description = "정산 원장 ID") String settlementId,
        @Schema(description = "정산 상태 (PENDING, HOLD, COMPLETED)") String status,
        @Schema(description = "주문 ID (내부 orderItemId → API orderId 매핑)") String orderId,
        @Schema(description = "주문 번호") String orderNumber,
        @Schema(description = "셀러 ID") long sellerId,
        @Schema(description = "금액 정보") SettlementAmountsApiResponse amounts,
        @Schema(description = "주문 일시") String orderedAt,
        @Schema(description = "배송 완료 일시") String deliveredAt,
        @Schema(description = "정산 예정일 (eligibleAt 기준)") String expectedSettlementDay,
        @Schema(description = "정산 완료일 (COMPLETED 상태일 때 updatedAt 기준)") String settlementDay,
        @Schema(description = "보류 정보 (보류 상태일 때만 존재)") HoldInfoApiResponse holdInfo) {

    /** 정산 금액 정보. */
    @Schema(description = "정산 금액 정보")
    public record SettlementAmountsApiResponse(
            @Schema(description = "판매 금액") int salesAmount,
            @Schema(description = "수수료 금액") int feeAmount,
            @Schema(description = "수수료율 (basis point, 1%=100)") int feeRate,
            @Schema(description = "정산 예정 금액") int expectedSettlementAmount,
            @Schema(description = "정산 금액") int settlementAmount) {}

    /** 보류 정보. */
    @Schema(description = "보류 정보")
    public record HoldInfoApiResponse(
            @Schema(description = "보류 사유") String holdReason,
            @Schema(description = "보류 처리 일시") String holdAt) {}
}
