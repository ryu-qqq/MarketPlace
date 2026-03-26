package com.ryuqq.marketplace.application.refund.dto.query;

import java.util.List;

/** 환불 목록 검색 파라미터. */
public record RefundSearchParams(
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
