package com.ryuqq.marketplace.application.legacy.productgroup.dto.response;

import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import java.util.List;

/**
 * 레거시 상품그룹 목록 조회 페이징 결과 DTO.
 *
 * <p>LegacySearchProductGroupByOffsetService → Adapter-In 전달용 결과 DTO입니다.
 *
 * @param items 상품그룹 상세 결과 목록
 * @param totalElements 전체 건수
 * @param page 현재 페이지 번호 (0-based)
 * @param size 페이지 크기
 */
public record LegacyProductGroupPageResult(
        List<LegacyProductGroupDetailResult> items, long totalElements, int page, int size) {

    public static LegacyProductGroupPageResult of(
            List<LegacyProductGroupDetailResult> items, long totalElements, int page, int size) {
        return new LegacyProductGroupPageResult(items, totalElements, page, size);
    }

    public static LegacyProductGroupPageResult empty(int page, int size) {
        return new LegacyProductGroupPageResult(List.of(), 0L, page, size);
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
