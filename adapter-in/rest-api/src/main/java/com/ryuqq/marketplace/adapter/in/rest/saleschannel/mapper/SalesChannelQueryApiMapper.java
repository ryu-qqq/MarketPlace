package com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.query.SearchSalesChannelsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannel Query API Mapper. */
@Component
public class SalesChannelQueryApiMapper {

    public SalesChannelSearchParams toSearchParams(SearchSalesChannelsApiRequest request) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        request.sortKey() != null ? request.sortKey() : "createdAt",
                        request.sortDirection() != null ? request.sortDirection() : "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return SalesChannelSearchParams.of(
                request.statuses(), request.searchField(), request.searchWord(), searchParams);
    }

    public SalesChannelApiResponse toResponse(SalesChannelResult result) {
        return new SalesChannelApiResponse(
                result.id(),
                result.channelName(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<SalesChannelApiResponse> toResponses(List<SalesChannelResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<SalesChannelApiResponse> toPageResponse(
            SalesChannelPageResult pageResult) {
        List<SalesChannelApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
