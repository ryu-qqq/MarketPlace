package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

/** 세토프 CreateOptionDetail 호환 요청 DTO. */
public record LegacyCreateOptionDetailRequest(
        Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}
