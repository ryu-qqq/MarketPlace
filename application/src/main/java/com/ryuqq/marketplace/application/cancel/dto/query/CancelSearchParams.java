package com.ryuqq.marketplace.application.cancel.dto.query;

import java.util.List;

/** 취소 목록 검색 파라미터. */
public record CancelSearchParams(
        List<String> statuses,
        List<String> types,
        String searchField,
        String searchWord,
        String dateField,
        String startDate,
        String endDate,
        String sortKey,
        String sortDirection,
        int page,
        int size) {}
