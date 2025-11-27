package com.ryuqq.marketplace.application.brand.port.in.query;

import com.ryuqq.marketplace.application.brand.dto.response.BrandDetailResponse;

public interface GetBrandUseCase {
    BrandDetailResponse getById(Long brandId);
    BrandDetailResponse getByCode(String code);
}
