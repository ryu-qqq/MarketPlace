package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/** 세토프 CreateProductImage 호환 요청 DTO. */
public record LegacyCreateProductImageRequest(
        @JsonProperty("type") String productImageType,
        @JsonProperty("productImageUrl") String imageUrl,
        @JsonProperty("originUrl") String originUrl) {}
