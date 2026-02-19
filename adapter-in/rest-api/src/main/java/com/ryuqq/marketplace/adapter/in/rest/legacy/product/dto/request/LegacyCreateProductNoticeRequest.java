package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 CreateProductNotice 호환 요청 DTO. */
public record LegacyCreateProductNoticeRequest(
        String material,
        String color,
        String size,
        String maker,
        String origin,
        String washingMethod,
        String yearMonth,
        String assuranceStandard,
        String asPhone) {}
