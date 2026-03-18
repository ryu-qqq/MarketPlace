package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 교환 재배송 일괄 처리 API 요청. */
public record ShipExchangeBatchApiRequest(@NotEmpty List<ShipItemApiRequest> items) {

    public record ShipItemApiRequest(
            @NotBlank String exchangeClaimId,
            @NotBlank String linkedOrderId,
            @NotBlank String deliveryCompany,
            @NotBlank String trackingNumber) {}
}
