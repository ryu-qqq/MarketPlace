package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** 세토프 OptionDto 호환 응답 DTO. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LegacyOptionDto(
        Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}
