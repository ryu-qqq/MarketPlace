package com.ryuqq.marketplace.application.outboundproduct.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 연동 이력 검색 파라미터 DTO.
 *
 * <p>APP-DTO-002: SearchParams는 CommonSearchParams를 필수 포함.
 */
public record SyncHistorySearchParams(
        long productGroupId, String status, CommonSearchParams commonSearchParams) {

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
