package com.ryuqq.marketplace.application.selleraddress.dto.query;

import java.util.List;

/**
 * 셀러 주소 검색 파라미터.
 *
 * @param sellerIds 셀러 ID 목록 (필수, 1건 이상)
 * @param addressTypes 주소 유형 필터 (null/empty면 전체)
 * @param defaultAddress 기본 주소 필터 (null이면 전체)
 * @param searchField 검색 필드 (null이면 keyword 미적용)
 * @param searchWord 검색어 (null/blank면 미적용)
 * @param page 페이지 번호 (0-based)
 * @param size 페이지 크기
 */
public record SellerAddressSearchParams(
        List<Long> sellerIds,
        List<String> addressTypes,
        Boolean defaultAddress,
        String searchField,
        String searchWord,
        int page,
        int size) {}
