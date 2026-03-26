package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 배송 목록 조회 응답 DTO (V4). */
@Schema(description = "배송 목록 조회 응답")
public record ShipmentListApiResponse(
        @Schema(description = "배송 정보") ShipmentInfoResponse shipment,
        @Schema(description = "주문 정보") OrderInfoResponse order,
        @Schema(description = "상품주문 정보") ProductOrderInfoResponse productOrder,
        @Schema(description = "수령인 정보") ReceiverInfoResponse receiver) {

    /** 배송 정보 응답. */
    @Schema(description = "배송 정보")
    public record ShipmentInfoResponse(
            @Schema(description = "배송 ID", example = "ship-001") String shipmentId,
            @Schema(description = "배송번호", example = "SHP-20260220-001") String shipmentNumber,
            @Schema(
                            description =
                                    "배송 상태 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED,"
                                            + " FAILED, CANCELLED)",
                            example = "SHIPPED")
                    String status,
            @Schema(description = "송장번호", example = "1234567890") String trackingNumber,
            @Schema(description = "택배사 코드", example = "CJ") String courierCode,
            @Schema(description = "택배사명", example = "CJ대한통운") String courierName,
            @Schema(description = "발주확인일시") String orderConfirmedAt,
            @Schema(description = "발송일시") String shippedAt,
            @Schema(description = "배송완료일시") String deliveredAt,
            @Schema(description = "등록일시") String createdAt) {}

    /** 주문 정보 응답. */
    @Schema(description = "주문 정보")
    public record OrderInfoResponse(
            @Schema(description = "주문 ID") String orderId,
            @Schema(description = "주문번호") String orderNumber,
            @Schema(description = "주문 상태") String status,
            @Schema(description = "판매채널 ID") long salesChannelId,
            @Schema(description = "샵 ID") long shopId,
            @Schema(description = "샵 코드") String shopCode,
            @Schema(description = "샵 이름") String shopName,
            @Schema(description = "외부 주문번호") String externalOrderNo,
            @Schema(description = "외부 주문일시") String externalOrderedAt,
            @Schema(description = "구매자명") String buyerName,
            @Schema(description = "구매자 이메일") String buyerEmail,
            @Schema(description = "구매자 전화번호") String buyerPhone,
            @Schema(description = "생성일시") String createdAt,
            @Schema(description = "수정일시") String updatedAt) {}

    /** 상품주문 정보 응답. */
    @Schema(description = "상품주문 정보")
    public record ProductOrderInfoResponse(
            @Schema(description = "주문 상품 ID (UUIDv7)") String orderItemId,
            @Schema(description = "내부 상품그룹 ID") long productGroupId,
            @Schema(description = "내부 상품 ID") long productId,
            @Schema(description = "SKU 코드") String skuCode,
            @Schema(description = "상품그룹명") String productGroupName,
            @Schema(description = "브랜드명 (스냅샷)") String brandName,
            @Schema(description = "셀러명 (스냅샷)") String sellerName,
            @Schema(description = "대표 이미지 URL") String mainImageUrl,
            @Schema(description = "외부 상품 ID") String externalProductId,
            @Schema(description = "외부 옵션 ID") String externalOptionId,
            @Schema(description = "외부 상품명") String externalProductName,
            @Schema(description = "외부 옵션명") String externalOptionName,
            @Schema(description = "외부 이미지 URL") String externalImageUrl,
            @Schema(description = "개당 판매가") int unitPrice,
            @Schema(description = "수량") int quantity,
            @Schema(description = "총 금액") int totalAmount,
            @Schema(description = "할인 금액") int discountAmount,
            @Schema(description = "실결제 금액") int paymentAmount) {}

    /** 수령인 정보 응답. */
    @Schema(description = "수령인 정보")
    public record ReceiverInfoResponse(
            @Schema(description = "수령인명") String receiverName,
            @Schema(description = "수령인 전화번호") String receiverPhone,
            @Schema(description = "수령인 우편번호") String receiverZipcode,
            @Schema(description = "수령인 주소") String receiverAddress,
            @Schema(description = "수령인 상세주소") String receiverAddressDetail,
            @Schema(description = "배송 요청사항") String deliveryRequest) {}
}
