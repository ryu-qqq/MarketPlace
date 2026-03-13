package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 송장등록 일괄 처리 요청 DTO. */
@Schema(description = "송장등록 일괄 처리 요청")
public record ShipBatchApiRequest(
        @Schema(description = "송장등록 대상 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                @Valid
                List<ShipBatchItemApiRequest> items) {

    /** 송장등록 개별 항목 요청. */
    @Schema(description = "송장등록 개별 항목")
    public record ShipBatchItemApiRequest(
            @Schema(
                            description = "상품주문 ID (UUIDv7)",
                            example = "0194abcd-0000-7000-8000-000000000001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull
                    String orderItemId,
            @Schema(
                            description = "송장번호",
                            example = "1234567890",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String trackingNumber,
            @Schema(
                            description = "택배사 코드",
                            example = "CJ",
                            requiredMode = Schema.RequiredMode.REQUIRED)
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
                            example = "COURIER",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank
                    String shipmentMethodType) {}
}
