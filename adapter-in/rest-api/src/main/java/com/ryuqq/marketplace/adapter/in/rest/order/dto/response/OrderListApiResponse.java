package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 주문 목록 항목 응답. */
@Schema(description = "주문 목록 항목")
public record OrderListApiResponse(
        @Schema(description = "주문 ID (UUIDv7)") String orderId,
        @Schema(description = "주문번호 (ORD-YYYYMMDD-XXXX)") String orderNumber,
        @Schema(description = "주문 상태") String status,
        @Schema(description = "판매채널 ID") long salesChannelId,
        @Schema(description = "외부 주문번호") String externalOrderNo,
        @Schema(description = "구매자명") String buyerName,
        @Schema(description = "상품 수") int itemCount,
        @Schema(description = "주문일시 (ISO 8601)") String orderedAt,
        @Schema(description = "생성일시 (ISO 8601)") String createdAt) {}
