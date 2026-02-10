package com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query.SearchBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset Query API Mapper. */
@Component
public class BrandPresetQueryApiMapper {

    public BrandPresetSearchParams toSearchParams(SearchBrandPresetsApiRequest request) {
        return new BrandPresetSearchParams(
                request.salesChannelIds(),
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                request.startDate(),
                request.endDate(),
                request.sortKey(),
                request.sortDirection(),
                request.page() != null ? request.page() : 0,
                request.size() != null ? request.size() : 20);
    }

    public BrandPresetApiResponse toResponse(BrandPresetResult result) {
        return new BrandPresetApiResponse(
                result.id(),
                result.shopId(),
                result.shopName(),
                result.salesChannelId(),
                result.salesChannelName(),
                result.accountId(),
                result.presetName(),
                result.brandName(),
                result.brandCode(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public List<BrandPresetApiResponse> toResponses(List<BrandPresetResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<BrandPresetApiResponse> toPageResponse(
            BrandPresetPageResult pageResult) {
        List<BrandPresetApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
