package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

/**
 * 브랜드 기본 정보 응답 DTO
 * RESTful API 응답으로 사용되는 브랜드 기본 정보 record
 */
public record BrandApiResponse(
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
    String logoUrl
) {}
