package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 단건 송장등록 요청 DTO. */
@Schema(description = "단건 송장등록 요청")
public record ShipSingleApiRequest(
        @Schema(
                        description = "송장번호",
                        example = "1234567890",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String trackingNumber,
        @Schema(description = "택배사 코드", example = "CJ", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String courierCode,
        @Schema(
                        description = "택배사명",
                        example = "CJ대한통운",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String courierName,
        @Schema(
                        description = "배송 방법 유형",
                        example = "PARCEL",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String shipmentMethodType) {}
