package com.ryuqq.marketplace.adapter.in.rest.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 주문 상세 조회 응답. */
@Schema(description = "주문 상세 정보")
public record OrderDetailApiResponse(
        @Schema(description = "주문 ID (UUIDv7)") String orderId,
        @Schema(description = "주문번호 (ORD-YYYYMMDD-XXXX)") String orderNumber,
        @Schema(description = "주문 상태") String status,
        @Schema(description = "판매채널 ID") long salesChannelId,
        @Schema(description = "샵 ID") long shopId,
        @Schema(description = "외부 주문번호") String externalOrderNo,
        @Schema(description = "외부 주문일시 (ISO 8601)") String externalOrderedAt,
        @Schema(description = "구매자 정보") BuyerInfoApiResponse buyerInfo,
        @Schema(description = "주문 상품 목록") List<OrderItemApiResponse> items,
        @Schema(description = "주문 타임라인") List<OrderTimeLineApiResponse> orderTimeLine,
        @Schema(description = "주문일시 (ISO 8601)") String orderedAt,
        @Schema(description = "생성일시 (ISO 8601)") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)") String updatedAt) {

    /** 구매자 정보 응답. */
    @Schema(description = "구매자 정보")
    public record BuyerInfoApiResponse(
            @Schema(description = "구매자명") String buyerName,
            @Schema(description = "이메일") String buyerEmail,
            @Schema(description = "연락처") String buyerPhoneNumber) {}

    /** 주문 상품 응답. */
    @Schema(description = "주문 상품 정보")
    public record OrderItemApiResponse(
            @Schema(description = "주문 상품 ID") long orderItemId,
            @Schema(description = "상품그룹 ID") long productGroupId,
            @Schema(description = "상품 ID") long productId,
            @Schema(description = "SKU 코드") String skuCode,
            @Schema(description = "외부 상품 ID") String externalProductId,
            @Schema(description = "외부 상품명") String externalProductName,
            @Schema(description = "외부 옵션명") String externalOptionName,
            @Schema(description = "외부 이미지 URL") String externalImageUrl,
            @Schema(description = "개당 판매가 (원)") int unitPrice,
            @Schema(description = "주문 수량") int quantity,
            @Schema(description = "실결제 금액 (원)") int paymentAmount,
            @Schema(description = "수령인명") String receiverName) {}

    /** 주문 타임라인 응답. */
    @Schema(description = "주문 타임라인 항목")
    public record OrderTimeLineApiResponse(
            @Schema(description = "이력 ID") long historyId,
            @Schema(description = "이전 상태") String fromStatus,
            @Schema(description = "변경된 상태") String toStatus,
            @Schema(description = "변경자") String changedBy,
            @Schema(description = "변경 사유") String reason,
            @Schema(description = "변경일시 (ISO 8601)") String changedAt) {}
}
