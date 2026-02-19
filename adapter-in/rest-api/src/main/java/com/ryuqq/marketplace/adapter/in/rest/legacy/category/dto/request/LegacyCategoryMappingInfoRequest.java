package com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/** 세토프 CategoryMappingInfo 호환 요청 DTO. */
public record LegacyCategoryMappingInfoRequest(
        @JsonProperty("mapping_category_id") String categoryMappingId,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("category_id") Long categoryId) {}
