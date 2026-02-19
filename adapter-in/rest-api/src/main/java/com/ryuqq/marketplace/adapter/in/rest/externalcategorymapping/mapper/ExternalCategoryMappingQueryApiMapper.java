package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.query.SearchExternalCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response.ExternalCategoryMappingApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Query API 변환 매퍼. */
@Component
public class ExternalCategoryMappingQueryApiMapper {

    public ExternalCategoryMappingSearchParams toSearchParams(
            Long externalSourceId, SearchExternalCategoryMappingsApiRequest request) {
        CommonSearchParams commonSearchParams =
                CommonSearchParams.of(
                        null,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());

        return new ExternalCategoryMappingSearchParams(
                externalSourceId,
                null,
                request.searchField(),
                request.searchWord(),
                commonSearchParams);
    }

    public ExternalCategoryMappingApiResponse toResponse(ExternalCategoryMappingResult result) {
        return new ExternalCategoryMappingApiResponse(
                result.id(),
                result.externalSourceId(),
                result.externalCategoryCode(),
                result.externalCategoryName(),
                result.internalCategoryId(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<ExternalCategoryMappingApiResponse> toResponses(
            List<ExternalCategoryMappingResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<ExternalCategoryMappingApiResponse> toPageResponse(
            ExternalCategoryMappingPageResult pageResult) {
        List<ExternalCategoryMappingApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
