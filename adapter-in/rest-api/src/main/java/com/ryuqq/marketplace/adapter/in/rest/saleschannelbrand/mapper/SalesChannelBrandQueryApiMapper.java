package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.query.SearchSalesChannelBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Query API Mapper. */
@Component
public class SalesChannelBrandQueryApiMapper {

    public SalesChannelBrandSearchParams toSearchParams(
            List<Long> salesChannelIds, SearchSalesChannelBrandsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return SalesChannelBrandSearchParams.of(
                salesChannelIds,
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    public SalesChannelBrandApiResponse toResponse(SalesChannelBrandResult result) {
        return new SalesChannelBrandApiResponse(
                result.id(),
                result.salesChannelId(),
                result.externalBrandCode(),
                result.externalBrandName(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<SalesChannelBrandApiResponse> toResponses(List<SalesChannelBrandResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<SalesChannelBrandApiResponse> toPageResponse(
            SalesChannelBrandPageResult pageResult) {
        List<SalesChannelBrandApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
