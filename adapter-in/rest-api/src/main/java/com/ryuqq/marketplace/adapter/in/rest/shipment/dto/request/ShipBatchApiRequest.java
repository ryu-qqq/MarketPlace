package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 송장등록 일괄 처리 요청 DTO. */
@Schema(description = "송장등록 일괄 처리 요청")
public record ShipBatchApiRequest(
        @Schema(description = "송장등록 대상 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                @Valid
                List<ShipBatchItemApiRequest> requests,
        @Schema(description = "메모") String memo) {

    /** 송장등록 개별 항목 요청. */
    @Schema(description = "송장등록 개별 항목")
    public record ShipBatchItemApiRequest(
            @Schema(
                            description = "주문번호",
                            example = "ORD-20260324-6159",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String orderNumber,
            @Schema(description = "배송 방법", requiredMode = Schema.RequiredMode.REQUIRED) @Valid
                    ShipMethodRequest method,
            @Schema(
                            description = "송장번호",
                            example = "1234567890",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String trackingNumber) {}

    /** 배송 방법 요청. */
    @Schema(description = "배송 방법")
    public record ShipMethodRequest(
            @Schema(description = "배송 유형", example = "COURIER") @NotBlank String type,
            @Schema(description = "택배사 코드", example = "CJ_LOGISTICS") @NotBlank
                    String courierCode) {}
}
