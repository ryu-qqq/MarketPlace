package com.ryuqq.marketplace.application.exchange.dto.query;

import java.util.List;

/** 교환 목록 검색 파라미터. */
public record ExchangeSearchParams(
        List<String> statuses,
        String searchField,
        String searchWord,
        String dateField,
        String startDate,
        String endDate,
        String sortKey,
        String sortDirection,
        int page,
        int size) {}
