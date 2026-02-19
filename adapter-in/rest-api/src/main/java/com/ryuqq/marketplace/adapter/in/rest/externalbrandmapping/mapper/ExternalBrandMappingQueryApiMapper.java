package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.query.SearchExternalBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response.ExternalBrandMappingApiResponse;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Query API 변환 매퍼. */
@Component
public class ExternalBrandMappingQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public ExternalBrandMappingSearchParams toSearchParams(
            Long externalSourceId, SearchExternalBrandMappingsApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        return new ExternalBrandMappingSearchParams(
                externalSourceId, null, request.searchWord(), page, size);
    }

    public ExternalBrandMappingApiResponse toResponse(ExternalBrandMappingResult result) {
        return new ExternalBrandMappingApiResponse(
                result.id(),
                result.externalSourceId(),
                result.externalBrandCode(),
                result.externalBrandName(),
                result.internalBrandId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<ExternalBrandMappingApiResponse> toResponses(
            List<ExternalBrandMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<ExternalBrandMappingApiResponse> toPageResponse(
            ExternalBrandMappingPageResult pageResult) {
        List<ExternalBrandMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
