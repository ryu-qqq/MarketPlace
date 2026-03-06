package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 주문 상태별 요약 응답. */
@Schema(description = "주문 상태별 건수 요약")
public record OrderSummaryApiResponse(
        @Schema(description = "주문접수") int ordered,
        @Schema(description = "발주확인") int preparing,
        @Schema(description = "출고완료") int shipped,
        @Schema(description = "배송완료") int delivered,
        @Schema(description = "구매확정") int confirmed,
        @Schema(description = "취소") int cancelled,
        @Schema(description = "클레임 진행중") int claimInProgress,
        @Schema(description = "환불완료") int refunded,
        @Schema(description = "교환완료") int exchanged) {}
