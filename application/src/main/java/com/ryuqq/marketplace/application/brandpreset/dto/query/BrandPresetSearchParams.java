package com.ryuqq.marketplace.application.brandpreset.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * BrandPreset 검색 파라미터 DTO.
 *
 * <p>APP-DTO-002: SearchParams는 CommonSearchParams를 필수 포함.
 */
public record BrandPresetSearchParams(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public static BrandPresetSearchParams of(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams commonSearchParams) {
        return new BrandPresetSearchParams(
                salesChannelIds, statuses, searchField, searchWord, commonSearchParams);
    }

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
