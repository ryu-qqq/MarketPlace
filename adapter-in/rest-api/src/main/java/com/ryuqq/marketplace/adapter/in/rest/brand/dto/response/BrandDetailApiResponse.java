package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

import java.util.List;

/**
 * 브랜드 상세 정보 응답 DTO (별칭 포함)
 * 브랜드 상세 조회 API 응답으로 사용되는 record
 * 기본 정보와 별칭 리스트를 포함
 */
public record BrandDetailApiResponse(
    Long id,
    String code,
    String canonicalName,
    String nameKo,
    String nameEn,
    String shortName,
    String country,
    String department,
    boolean isLuxury,
    String status,
    String officialWebsite,
    String logoUrl,
    String description,
    String dataQualityLevel,
    int dataQualityScore,
    List<BrandAliasApiResponse> aliases
) {}
