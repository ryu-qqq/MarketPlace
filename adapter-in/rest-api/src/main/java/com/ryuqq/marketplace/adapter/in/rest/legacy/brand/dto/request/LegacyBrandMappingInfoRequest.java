package com.ryuqq.marketplace.adapter.in.rest.legacy.brand.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/** 세토프 BrandMappingInfo 호환 요청 DTO. */
public record LegacyBrandMappingInfoRequest(
        @JsonProperty("mapping_brand_id") String brandMappingId,
        @JsonProperty("brand_name") String brandName,
        @JsonProperty("brand_id") Long brandId) {}
