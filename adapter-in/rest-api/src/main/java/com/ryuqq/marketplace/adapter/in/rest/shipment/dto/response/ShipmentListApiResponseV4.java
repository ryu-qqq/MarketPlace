package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 배송 목록 조회 응답 (V4 프론트 스펙).
 *
 * <p>프론트 OMS 배송 관리 테이블 컬럼 구조에 맞춘 플랫 응답. OrderProductApiResponse는 통합주문(orders) V4와 동일한 구조를 사용합니다.
 */
@Schema(description = "배송 목록 조회 응답 (V4)")
public record ShipmentListApiResponseV4(
        @Schema(description = "주문 ID (UUIDv7)") String orderId,
        @Schema(description = "주문번호") String orderNumber,
        @Schema(description = "배송번호") String shipmentNumber,
        @Schema(description = "배송 상태") String status,
        @Schema(description = "송장번호") String trackingNumber,
        @Schema(description = "택배사 코드") String courierCode,
        @Schema(description = "발주확인일시") String orderConfirmedAt,
        @Schema(description = "발송일시") String shippedAt,
        @Schema(description = "배송완료일시") String deliveredAt,
        @Schema(description = "등록일시") String createdAt,
        @Schema(description = "배송 방법") ShipmentMethodV4 shipmentMethod,
        @Schema(description = "주문 상품 정보")
                OrderListApiResponseV4.OrderProductApiResponse orderProduct,
        @Schema(description = "수령인 정보") ReceiverInfoV4 receiverInfo,
        @Schema(description = "외부몰 주문 정보") ExternalOrderInfoV4 externalOrderInfo,
        @Schema(description = "취소 정보") CancelInfoV4 cancelInfo) {

    @Schema(description = "배송 방법 (V4)")
    public record ShipmentMethodV4(
            @Schema(description = "배송 방법 유형 (COURIER, PARCEL)") String type,
            @Schema(description = "택배사명") String courierName) {}

    @Schema(description = "수령인 정보 (V4)")
    public record ReceiverInfoV4(
            @Schema(description = "수령인명") String receiverName,
            @Schema(description = "수령인 연락처") String receiverPhoneNumber,
            @Schema(description = "주소") String address,
            @Schema(description = "상세 주소") String addressDetail,
            @Schema(description = "우편번호") String zipCode) {}

    @Schema(description = "외부몰 주문 정보 (V4)")
    public record ExternalOrderInfoV4(
            @Schema(description = "샵 코드") String shopCode,
            @Schema(description = "외부몰 주문번호") String shopOrderNo) {}

    @Schema(description = "취소 정보 (V4)")
    public record CancelInfoV4(@Schema(description = "취소 ID") String cancelId) {}
}
