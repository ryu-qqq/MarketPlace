package com.ryuqq.marketplace.application.brand.port.out.query;

import java.util.List;

public interface BrandAliasQueryPort {
    List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias);
    List<BrandAliasProjection> searchByKeyword(String keyword);
    List<BrandAliasProjection> findByBrandId(Long brandId);
}
