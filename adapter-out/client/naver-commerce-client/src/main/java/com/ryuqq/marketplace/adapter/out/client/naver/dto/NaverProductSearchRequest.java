package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 네이버 커머스 상품 목록 조회 요청 DTO.
 *
 * <p>POST /v1/products/search 요청 본문.
 *
 * @see <a href="https://apicenter.commerce.naver.com/docs/commerce-api/current/search-product">상품
 *     목록 조회</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverProductSearchRequest(
        String searchKeywordType,
        List<Long> channelProductNos,
        List<Long> originProductNos,
        List<Long> groupProductNos,
        String sellerManagementCode,
        List<String> productStatusTypes,
        Integer page,
        Integer size,
        String orderType,
        String periodType,
        String fromDate,
        String toDate) {

    /**
     * 전체 상품 조회용 팩토리 메서드.
     *
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (최대 500)
     * @return 전체 상품 조회 요청
     */
    public static NaverProductSearchRequest allProducts(int page, int size) {
        return new NaverProductSearchRequest(
                null, null, null, null, null, null, page, size, "REG_DATE", null, null, null);
    }
}
