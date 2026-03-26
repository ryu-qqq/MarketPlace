package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.OrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ProductOrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ReceiverInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ShipmentInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 배송 상세 조회 응답 DTO (V4). */
@Schema(description = "배송 상세 조회 응답")
public record ShipmentDetailApiResponse(
        @Schema(description = "배송 정보") ShipmentInfoResponse shipment,
        @Schema(description = "주문 정보") OrderInfoResponse order,
        @Schema(description = "상품주문 정보") ProductOrderInfoResponse productOrder,
        @Schema(description = "수령인 정보") ReceiverInfoResponse receiver,
        @Schema(description = "결제 정보") PaymentInfoResponse payment) {

    /** 결제 정보 응답. */
    @Schema(description = "결제 정보")
    public record PaymentInfoResponse(
            @Schema(description = "결제 ID") String paymentId,
            @Schema(description = "결제 번호") String paymentNumber,
            @Schema(description = "결제 상태") String paymentStatus,
            @Schema(description = "결제 수단") String paymentMethod,
            @Schema(description = "PG사 결제 ID") String paymentAgencyId,
            @Schema(description = "결제 금액") int paymentAmount,
            @Schema(description = "결제일시") String paidAt,
            @Schema(description = "결제취소일시") String canceledAt) {}
}
