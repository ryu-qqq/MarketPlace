package com.ryuqq.marketplace.application.brand.port.out.query;

import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import com.ryuqq.marketplace.application.common.dto.response.PageResponse;

import java.util.List;
import java.util.Optional;

public interface BrandQueryPort {
    Optional<Brand> findById(Long brandId);
    Optional<Brand> findByCode(String code);
    PageResponse<Brand> search(BrandSearchQuery query, int page, int size);
    List<Brand> findByIds(List<Long> brandIds);
    List<Brand> findAll(BrandSearchQuery query);
}
