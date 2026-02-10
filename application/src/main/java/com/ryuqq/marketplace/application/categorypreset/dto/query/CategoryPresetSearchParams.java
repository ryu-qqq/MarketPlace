package com.ryuqq.marketplace.application.categorypreset.dto.query;

import java.util.List;

/** 카테고리 프리셋 검색 파라미터 DTO. */
public record CategoryPresetSearchParams(
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
