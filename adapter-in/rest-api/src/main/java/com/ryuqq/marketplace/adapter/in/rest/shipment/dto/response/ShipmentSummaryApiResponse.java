package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 배송 상태별 요약 응답 DTO. */
@Schema(description = "배송 상태별 요약 응답")
public record ShipmentSummaryApiResponse(
        @Schema(description = "배송 준비 대기") int ready,
        @Schema(description = "배송 준비 중") int preparing,
        @Schema(description = "발송 완료") int shipped,
        @Schema(description = "배송 중") int inTransit,
        @Schema(description = "배송 완료") int delivered,
        @Schema(description = "배송 실패") int failed,
        @Schema(description = "취소") int cancelled) {}
