package com.ryuqq.marketplace.application.outboundproduct.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/** OMS 파트너(셀러) 검색 파라미터. */
public record OmsPartnerSearchParams(String keyword, CommonSearchParams searchParams) {

    public static OmsPartnerSearchParams of(String keyword, CommonSearchParams searchParams) {
        return new OmsPartnerSearchParams(keyword, searchParams);
    }

    public int page() {
        return searchParams.page();
    }

    public int size() {
        return searchParams.size();
    }

    public String sortKey() {
        return searchParams.sortKey();
    }

    public String sortDirection() {
        return searchParams.sortDirection();
    }
}
