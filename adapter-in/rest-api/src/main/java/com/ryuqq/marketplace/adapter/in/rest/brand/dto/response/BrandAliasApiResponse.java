package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

import java.math.BigDecimal;

/**
 * 브랜드 별칭 응답 DTO
 * 브랜드 별칭 정보를 RESTful API 응답으로 변환하는 record
 */
public record BrandAliasApiResponse(
    Long id,
    Long brandId,
    String originalAlias,
    String normalizedAlias,
    String sourceType,
    Long sellerId,
    String mallCode,
    BigDecimal confidence,
    String status
) {}
