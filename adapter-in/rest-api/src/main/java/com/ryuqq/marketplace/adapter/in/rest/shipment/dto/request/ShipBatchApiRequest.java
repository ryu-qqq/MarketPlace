package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 송장등록 일괄 처리 요청 DTO. */
@Schema(description = "송장등록 일괄 처리 요청")
public record ShipBatchApiRequest(
        @Schema(description = "송장등록 대상 목록") @NotEmpty @Valid List<ShipBatchItemApiRequest> items) {

    /** 송장등록 개별 항목 요청. */
    @Schema(description = "송장등록 개별 항목")
    public record ShipBatchItemApiRequest(
            @Schema(description = "배송 ID") @NotBlank String shipmentId,
            @Schema(description = "송장번호") @NotBlank String trackingNumber,
            @Schema(description = "택배사 코드") @NotBlank String courierCode,
            @Schema(description = "택배사명") @NotBlank String courierName,
            @Schema(description = "배송 방법 유형") @NotBlank String shipmentMethodType) {}
}
