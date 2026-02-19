package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 CreateClothesDetail 호환 요청 DTO. */
public record LegacyCreateClothesDetailRequest(
        String productCondition, String origin, String styleCode) {}
