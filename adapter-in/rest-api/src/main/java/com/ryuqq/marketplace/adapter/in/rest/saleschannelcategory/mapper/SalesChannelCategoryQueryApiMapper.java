package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query.SearchSalesChannelCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Query API Mapper. */
@Component
public class SalesChannelCategoryQueryApiMapper {

    public SalesChannelCategorySearchParams toSearchParams(
            List<Long> salesChannelIds, SearchSalesChannelCategoriesApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return SalesChannelCategorySearchParams.of(
                salesChannelIds,
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    public SalesChannelCategoryApiResponse toResponse(SalesChannelCategoryResult result) {
        return new SalesChannelCategoryApiResponse(
                result.id(),
                result.salesChannelId(),
                result.externalCategoryCode(),
                result.externalCategoryName(),
                result.parentId(),
                result.depth(),
                result.path(),
                result.sortOrder(),
                result.leaf(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<SalesChannelCategoryApiResponse> toResponses(
            List<SalesChannelCategoryResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<SalesChannelCategoryApiResponse> toPageResponse(
            SalesChannelCategoryPageResult pageResult) {
        List<SalesChannelCategoryApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
