package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import org.hibernate.validator.constraints.Length;

/** 세토프 CreateOptionDetail 호환 요청 DTO. */
public record LegacyCreateOptionDetailRequest(
        Long optionGroupId,
        Long optionDetailId,
        String optionName,
        @Length(max = 100, message = "옵션 값은 100자를 초과할 수 없습니다.") String optionValue) {}
