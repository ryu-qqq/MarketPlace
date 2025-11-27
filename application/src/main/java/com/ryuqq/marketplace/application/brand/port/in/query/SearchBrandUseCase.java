package com.ryuqq.marketplace.application.brand.port.in.query;

import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandSimpleResponse;
import com.ryuqq.marketplace.application.common.dto.response.PageResponse;

import java.util.List;

public interface SearchBrandUseCase {
    PageResponse<BrandResponse> search(BrandSearchQuery query, int page, int size);
    List<BrandSimpleResponse> getSimpleList();
}
