package com.ryuqq.marketplace.adapter.in.rest.brandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.query.SearchBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.response.BrandMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.brandmapping.dto.query.BrandMappingSearchParams;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingPageResult;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandMapping Query API Mapper. */
@Component
public class BrandMappingQueryApiMapper {

    public BrandMappingSearchParams toSearchParams(SearchBrandMappingsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return BrandMappingSearchParams.of(
                request.salesChannelBrandIds(),
                request.internalBrandIds(),
                request.salesChannelIds(),
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    public BrandMappingApiResponse toResponse(BrandMappingResult result) {
        return new BrandMappingApiResponse(
                result.id(),
                result.salesChannelBrandId(),
                result.internalBrandId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<BrandMappingApiResponse> toResponses(List<BrandMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<BrandMappingApiResponse> toPageResponse(
            BrandMappingPageResult pageResult) {
        List<BrandMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
