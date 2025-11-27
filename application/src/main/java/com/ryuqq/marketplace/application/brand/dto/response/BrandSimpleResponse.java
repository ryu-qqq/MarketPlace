package com.ryuqq.marketplace.application.brand.dto.response;

public record BrandSimpleResponse(
    Long brandId,
    String code,
    String nameKo,
    String nameEn
) {
}
