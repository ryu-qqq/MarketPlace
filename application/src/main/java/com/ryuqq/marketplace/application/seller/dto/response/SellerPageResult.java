package com.ryuqq.marketplace.application.seller.dto.response;

import java.util.List;

/**
 * 셀러 페이지 조회 결과.
 *
 * @param content 셀러 목록
 * @param totalCount 전체 개수
 * @param page 현재 페이지
 * @param size 페이지 크기
 * @param hasNext 다음 페이지 존재 여부
 */
public record SellerPageResult(
        List<SellerResult> content, long totalCount, int page, int size, boolean hasNext) {

    public SellerPageResult {
        content = content != null ? List.copyOf(content) : List.of();
    }

    public static SellerPageResult of(
            List<SellerResult> content, long totalCount, int page, int size) {
        boolean hasNext = (long) (page + 1) * size < totalCount;
        return new SellerPageResult(content, totalCount, page, size, hasNext);
    }
}
