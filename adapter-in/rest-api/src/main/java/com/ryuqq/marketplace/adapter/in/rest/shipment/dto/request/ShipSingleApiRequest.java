package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 단건 송장등록 요청 DTO. */
@Schema(description = "단건 송장등록 요청")
public record ShipSingleApiRequest(
        @Schema(description = "송장번호") @NotBlank String trackingNumber,
        @Schema(description = "택배사 코드") @NotBlank String courierCode,
        @Schema(description = "택배사명") @NotBlank String courierName,
        @Schema(description = "배송 방법 유형") @NotBlank String shipmentMethodType) {}
