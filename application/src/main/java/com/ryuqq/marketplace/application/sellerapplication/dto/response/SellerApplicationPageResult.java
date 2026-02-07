package com.ryuqq.marketplace.application.sellerapplication.dto.response;

import java.util.List;

/**
 * 셀러 입점 신청 페이지 조회 결과.
 *
 * @param content 입점 신청 목록
 * @param totalCount 전체 개수
 * @param page 현재 페이지
 * @param size 페이지 크기
 * @param hasNext 다음 페이지 존재 여부
 */
public record SellerApplicationPageResult(
        List<SellerApplicationResult> content,
        long totalCount,
        int page,
        int size,
        boolean hasNext) {

    public static SellerApplicationPageResult of(
            List<SellerApplicationResult> content, long totalCount, int page, int size) {
        boolean hasNext = (long) (page + 1) * size < totalCount;
        return new SellerApplicationPageResult(content, totalCount, page, size, hasNext);
    }

    public static SellerApplicationPageResult empty(int page, int size) {
        return new SellerApplicationPageResult(List.of(), 0L, page, size, false);
    }
}
