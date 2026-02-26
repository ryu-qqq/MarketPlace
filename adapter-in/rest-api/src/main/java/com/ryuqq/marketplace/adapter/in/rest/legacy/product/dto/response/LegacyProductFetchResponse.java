package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.Set;

/** 세토프 ProductFetchResponse 호환 응답 DTO. */
public record LegacyProductFetchResponse(
        long productId,
        int stockQuantity,
        LegacyProductStatusResponse productStatus,
        String option,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) Set<LegacyOptionDto> options,
        BigDecimal additionalPrice) {}
