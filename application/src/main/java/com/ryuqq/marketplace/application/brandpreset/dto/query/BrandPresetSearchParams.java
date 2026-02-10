package com.ryuqq.marketplace.application.brandpreset.dto.query;

import java.util.List;

/** BrandPreset 검색 파라미터 DTO. */
public record BrandPresetSearchParams(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        String startDate,
        String endDate,
        String sortKey,
        String sortDirection,
        Integer page,
        Integer size) {}
