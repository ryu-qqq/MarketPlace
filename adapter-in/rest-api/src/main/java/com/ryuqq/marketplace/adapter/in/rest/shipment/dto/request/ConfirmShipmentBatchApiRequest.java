package com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 발주확인 일괄 처리 요청 DTO. */
@Schema(description = "발주확인 일괄 처리 요청")
public record ConfirmShipmentBatchApiRequest(
        @Schema(
                        description = "발주확인 대상 상품주문 ID 목록",
                        example = "[1001, 1002]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                List<Long> orderItemIds) {}
