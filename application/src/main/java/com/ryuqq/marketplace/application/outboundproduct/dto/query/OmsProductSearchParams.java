package com.ryuqq.marketplace.application.outboundproduct.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * OMS 상품 목록 검색 파라미터 DTO.
 *
 * <p>APP-DTO-002: SearchParams는 CommonSearchParams를 필수 포함.
 */
public record OmsProductSearchParams(
        String dateType,
        List<String> statuses,
        List<String> syncStatuses,
        String searchField,
        String searchWord,
        List<Long> shopIds,
        List<Long> partnerIds,
        List<String> productCodes,
        CommonSearchParams commonSearchParams) {

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
