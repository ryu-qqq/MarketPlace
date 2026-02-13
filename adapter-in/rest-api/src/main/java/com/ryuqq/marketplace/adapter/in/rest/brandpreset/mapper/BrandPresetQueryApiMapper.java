package com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query.SearchBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset Query API Mapper. */
@Component
public class BrandPresetQueryApiMapper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BrandPresetSearchParams toSearchParams(SearchBrandPresetsApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        parseDate(request.startDate()),
                        parseDate(request.endDate()),
                        request.sortKey(),
                        request.sortDirection(),
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 20);

        return BrandPresetSearchParams.of(
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

    public BrandPresetDetailApiResponse toDetailResponse(BrandPresetDetailResult result) {
        BrandPresetDetailApiResponse.MappingBrandResponse mappingBrand =
                new BrandPresetDetailApiResponse.MappingBrandResponse(
                        result.mappingBrand().brandCode(), result.mappingBrand().brandName());

        List<BrandPresetDetailApiResponse.InternalBrandResponse> internalBrands =
                result.internalBrands().stream()
                        .map(
                                ib ->
                                        new BrandPresetDetailApiResponse.InternalBrandResponse(
                                                ib.id(), ib.brandName()))
                        .toList();

        return new BrandPresetDetailApiResponse(
                result.id(),
                result.shopId(),
                result.shopName(),
                result.salesChannelId(),
                result.salesChannelName(),
                result.accountId(),
                result.presetName(),
                mappingBrand,
                internalBrands,
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }
}
