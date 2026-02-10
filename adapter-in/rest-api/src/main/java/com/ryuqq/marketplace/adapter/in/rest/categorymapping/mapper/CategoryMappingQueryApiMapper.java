package com.ryuqq.marketplace.adapter.in.rest.categorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.query.SearchCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.response.CategoryMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.categorymapping.dto.query.CategoryMappingSearchParams;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingPageResult;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryMapping Query API Mapper. */
@Component
public class CategoryMappingQueryApiMapper {

    public CategoryMappingSearchParams toSearchParams(SearchCategoryMappingsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return CategoryMappingSearchParams.of(
                request.salesChannelCategoryIds(),
                request.internalCategoryIds(),
                request.salesChannelIds(),
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    public CategoryMappingApiResponse toResponse(CategoryMappingResult result) {
        return new CategoryMappingApiResponse(
                result.id(),
                result.salesChannelCategoryId(),
                result.internalCategoryId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<CategoryMappingApiResponse> toResponses(List<CategoryMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<CategoryMappingApiResponse> toPageResponse(
            CategoryMappingPageResult pageResult) {
        List<CategoryMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
