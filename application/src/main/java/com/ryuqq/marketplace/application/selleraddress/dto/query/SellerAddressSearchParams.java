package com.ryuqq.marketplace.application.selleraddress.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 셀러 주소 검색 파라미터.
 *
 * <p>APP-DTO-002: SearchParams는 CommonSearchParams를 필수 포함.
 *
 * @param sellerIds 셀러 ID 목록 (필수, 1건 이상)
 * @param addressTypes 주소 유형 필터 (null/empty면 전체)
 * @param defaultAddress 기본 주소 필터 (null이면 전체)
 * @param searchField 검색 필드 (null이면 keyword 미적용)
 * @param searchWord 검색어 (null/blank면 미적용)
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 */
public record SellerAddressSearchParams(
        List<Long> sellerIds,
        List<String> addressTypes,
        Boolean defaultAddress,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public static SellerAddressSearchParams of(
            List<Long> sellerIds,
            List<String> addressTypes,
            Boolean defaultAddress,
            String searchField,
            String searchWord,
            CommonSearchParams commonSearchParams) {
        return new SellerAddressSearchParams(
                sellerIds,
                addressTypes,
                defaultAddress,
                searchField,
                searchWord,
                commonSearchParams);
    }

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
