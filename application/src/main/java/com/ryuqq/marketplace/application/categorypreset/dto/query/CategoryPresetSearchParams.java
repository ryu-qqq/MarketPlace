package com.ryuqq.marketplace.application.categorypreset.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 카테고리 프리셋 검색 파라미터 DTO.
 *
 * <p>APP-DTO-002: SearchParams는 CommonSearchParams를 필수 포함.
 */
public record CategoryPresetSearchParams(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public static CategoryPresetSearchParams of(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams commonSearchParams) {
        return new CategoryPresetSearchParams(
                salesChannelIds, statuses, searchField, searchWord, commonSearchParams);
    }

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
