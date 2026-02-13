package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 배송 목록 조회 응답 DTO. */
@Schema(description = "배송 목록 조회 응답")
public record ShipmentListApiResponse(
        @Schema(description = "배송 ID") String shipmentId,
        @Schema(description = "배송번호") String shipmentNumber,
        @Schema(description = "주문 ID") String orderId,
        @Schema(description = "주문번호") String orderNumber,
        @Schema(description = "배송 상태") String status,
        @Schema(description = "송장번호") String trackingNumber,
        @Schema(description = "택배사명") String courierName,
        @Schema(description = "발송일시") String shippedAt,
        @Schema(description = "배송완료일시") String deliveredAt,
        @Schema(description = "등록일시") String createdAt) {}
