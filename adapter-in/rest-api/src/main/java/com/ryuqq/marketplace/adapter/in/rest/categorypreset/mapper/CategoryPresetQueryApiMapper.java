package com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper;

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.query.SearchCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryPreset Query API Mapper. */
@Component
public class CategoryPresetQueryApiMapper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CategoryPresetSearchParams toSearchParams(SearchCategoryPresetsApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        parseDate(request.startDate()),
                        parseDate(request.endDate()),
                        request.sortKey(),
                        request.sortDirection(),
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return CategoryPresetSearchParams.of(
                request.salesChannelIds(),
                request.statuses(),
                request.searchField(),
                request.searchWord(),
                commonParams);
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public CategoryPresetApiResponse toResponse(CategoryPresetResult result) {
        return new CategoryPresetApiResponse(
                result.id(),
                result.shopId(),
                result.shopName(),
                result.salesChannelId(),
                result.salesChannelName(),
                result.accountId(),
                result.presetName(),
                result.categoryPath(),
                result.categoryCode(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public List<CategoryPresetApiResponse> toResponses(List<CategoryPresetResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<CategoryPresetApiResponse> toPageResponse(
            CategoryPresetPageResult pageResult) {
        List<CategoryPresetApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
