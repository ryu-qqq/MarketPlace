package com.ryuqq.marketplace.application.brand.port.in.query;

import com.ryuqq.marketplace.application.brand.dto.response.BrandAliasResponse;

import java.util.List;

public interface GetBrandAliasesUseCase {
    List<BrandAliasResponse> getAliases(Long brandId);
    List<BrandAliasResponse> searchAliases(String keyword);
}
