package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.query.SearchInboundCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response.InboundCategoryMappingApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping Query API 변환 매퍼. */
@Component
public class InboundCategoryMappingQueryApiMapper {

    public InboundCategoryMappingSearchParams toSearchParams(
            Long inboundSourceId, SearchInboundCategoryMappingsApiRequest request) {
        CommonSearchParams commonSearchParams =
                CommonSearchParams.of(
                        null,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());

        return new InboundCategoryMappingSearchParams(
                inboundSourceId,
                null,
                request.searchField(),
                request.searchWord(),
                commonSearchParams);
    }

    public InboundCategoryMappingApiResponse toResponse(InboundCategoryMappingResult result) {
        return new InboundCategoryMappingApiResponse(
                result.id(),
                result.inboundSourceId(),
                result.externalCategoryCode(),
                result.externalCategoryName(),
                result.internalCategoryId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<InboundCategoryMappingApiResponse> toResponses(
            List<InboundCategoryMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<InboundCategoryMappingApiResponse> toPageResponse(
            InboundCategoryMappingPageResult pageResult) {
        List<InboundCategoryMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
