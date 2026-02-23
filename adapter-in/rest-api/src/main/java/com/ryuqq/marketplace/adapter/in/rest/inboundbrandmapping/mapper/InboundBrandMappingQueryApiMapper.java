package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.query.SearchInboundBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response.InboundBrandMappingApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundBrandMapping Query API 변환 매퍼. */
@Component
public class InboundBrandMappingQueryApiMapper {

    public InboundBrandMappingSearchParams toSearchParams(
            Long inboundSourceId, SearchInboundBrandMappingsApiRequest request) {
        CommonSearchParams commonSearchParams =
                CommonSearchParams.of(
                        null,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());

        return new InboundBrandMappingSearchParams(
                inboundSourceId,
                null,
                request.searchField(),
                request.searchWord(),
                commonSearchParams);
    }

    public InboundBrandMappingApiResponse toResponse(InboundBrandMappingResult result) {
        return new InboundBrandMappingApiResponse(
                result.id(),
                result.inboundSourceId(),
                result.externalBrandCode(),
                result.externalBrandName(),
                result.internalBrandId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<InboundBrandMappingApiResponse> toResponses(
            List<InboundBrandMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<InboundBrandMappingApiResponse> toPageResponse(
            InboundBrandMappingPageResult pageResult) {
        List<InboundBrandMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
