package com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.query.SearchExternalSourcesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response.ExternalSourceApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalSource Query API 변환 매퍼. */
@Component
public class ExternalSourceQueryApiMapper {

    public ExternalSourceSearchParams toSearchParams(SearchExternalSourcesApiRequest request) {
        CommonSearchParams commonSearchParams =
                CommonSearchParams.of(
                        null,
                        null,
                        null,
                        request.sortKey(),
                        request.sortDirection(),
                        request.page(),
                        request.size());

        return new ExternalSourceSearchParams(
                request.types(),
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                commonSearchParams);
    }

    public ExternalSourceApiResponse toResponse(ExternalSourceResult result) {
        return new ExternalSourceApiResponse(
                result.id(),
                result.code(),
                result.name(),
                result.type(),
                result.status(),
                result.description(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<ExternalSourceApiResponse> toResponses(List<ExternalSourceResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<ExternalSourceApiResponse> toPageResponse(
            ExternalSourcePageResult pageResult) {
        List<ExternalSourceApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
