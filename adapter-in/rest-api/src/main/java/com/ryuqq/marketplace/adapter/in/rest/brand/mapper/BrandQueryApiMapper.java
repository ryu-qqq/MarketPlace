package com.ryuqq.marketplace.adapter.in.rest.brand.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.SearchBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** Brand Query API Mapper. */
@Component
public class BrandQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public BrandSearchParams toSearchParams(SearchBrandsApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return BrandSearchParams.of(
                request.statuses(), request.searchField(), request.searchWord(), searchParams);
    }

    public BrandApiResponse toResponse(BrandResult result) {
        return new BrandApiResponse(
                result.id(),
                result.code(),
                result.nameKo(),
                result.nameEn(),
                result.shortName(),
                result.status(),
                result.logoUrl(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<BrandApiResponse> toResponses(List<BrandResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<BrandApiResponse> toPageResponse(BrandPageResult pageResult) {
        List<BrandApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
