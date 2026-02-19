package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 배송 목록 조회 응답 DTO. */
@Schema(description = "배송 목록 조회 응답")
public record ShipmentListApiResponse(
        @Schema(description = "배송 ID", example = "ship-001") String shipmentId,
        @Schema(description = "배송번호", example = "SHP-20260220-001") String shipmentNumber,
        @Schema(description = "주문 ID", example = "ord-001") String orderId,
        @Schema(description = "주문번호", example = "ORD-20260220-001") String orderNumber,
        @Schema(
                        description =
                                "배송 상태 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED,"
                                        + " CANCELLED)",
                        example = "SHIPPED")
                String status,
        @Schema(description = "송장번호", example = "1234567890") String trackingNumber,
        @Schema(description = "택배사명", example = "CJ대한통운") String courierName,
        @Schema(description = "발송일시", example = "2026-02-20T10:00:00+09:00") String shippedAt,
        @Schema(description = "배송완료일시", example = "2026-02-21T14:00:00+09:00") String deliveredAt,
        @Schema(description = "등록일시", example = "2026-02-20T08:00:00+09:00") String createdAt) {}
