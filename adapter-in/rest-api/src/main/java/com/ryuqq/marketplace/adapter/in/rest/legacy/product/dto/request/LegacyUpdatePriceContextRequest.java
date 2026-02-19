package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import java.util.List;

/** 세토프 UpdatePriceContext 호환 요청 DTO. */
public record LegacyUpdatePriceContextRequest(List<LegacyPriceCommandRequest> priceCommands) {

    public LegacyUpdatePriceContextRequest {
        priceCommands = priceCommands == null ? List.of() : List.copyOf(priceCommands);
    }

    /** 개별 가격 변경 항목. */
    public record LegacyPriceCommandRequest(
            long productGroupId, long regularPrice, long currentPrice) {}
}
