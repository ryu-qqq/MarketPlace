package com.ryuqq.marketplace.application.outboundproduct.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/** OMS 쇼핑몰 검색 파라미터. */
public record OmsShopSearchParams(String keyword, CommonSearchParams searchParams) {

    public static OmsShopSearchParams of(String keyword, CommonSearchParams searchParams) {
        return new OmsShopSearchParams(keyword, searchParams);
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
