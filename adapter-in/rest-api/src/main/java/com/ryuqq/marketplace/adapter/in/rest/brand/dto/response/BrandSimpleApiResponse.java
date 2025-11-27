package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

/**
 * 브랜드 간단 정보 응답 DTO (셀렉트박스용)
 * UI 드롭다운 또는 자동완성에서 사용되는 최소 정보 record
 */
public record BrandSimpleApiResponse(
    Long id,
    String code,
    String nameKo,
    String nameEn
) {}
