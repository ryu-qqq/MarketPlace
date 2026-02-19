package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

/** 세토프 CreateOption 호환 요청 DTO. */
public record LegacyCreateOptionRequest(
        Long productId,
        @JsonProperty("stockQuantity") Integer quantity,
        BigDecimal additionalPrice,
        List<LegacyCreateOptionDetailRequest> options) {

    public LegacyCreateOptionRequest {
        options = options == null ? List.of() : List.copyOf(options);
    }
}
