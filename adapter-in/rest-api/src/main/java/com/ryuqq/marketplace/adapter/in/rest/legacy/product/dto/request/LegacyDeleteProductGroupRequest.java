package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import java.util.List;

/** 세토프 DeleteProductGroup 호환 요청 DTO. */
public record LegacyDeleteProductGroupRequest(List<Long> productGroupIds) {

    public LegacyDeleteProductGroupRequest {
        productGroupIds = productGroupIds == null ? List.of() : List.copyOf(productGroupIds);
    }
}
