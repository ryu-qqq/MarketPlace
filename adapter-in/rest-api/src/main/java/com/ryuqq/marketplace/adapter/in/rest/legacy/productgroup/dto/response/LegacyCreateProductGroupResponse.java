package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response;

import java.util.List;

/** 세토프 CreateProductGroupResponse 호환 응답 DTO. */
public record LegacyCreateProductGroupResponse(
        long productGroupId, long sellerId, List<Long> productIds) {}
