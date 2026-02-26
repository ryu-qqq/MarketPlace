package com.ryuqq.marketplace.application.commoncodetype.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 공통 코드 타입 페이징 조회 결과 DTO.
 *
 * <p>페이징 조회 결과와 메타 정보를 함께 반환합니다.
 *
 * @param results 조회 결과 목록
 * @param pageMeta 페이징 메타 정보
 */
public record CommonCodeTypePageResult(List<CommonCodeTypeResult> results, PageMeta pageMeta) {

    public static CommonCodeTypePageResult of(
            List<CommonCodeTypeResult> results, PageMeta pageMeta) {
        return new CommonCodeTypePageResult(results, pageMeta);
    }

    public static CommonCodeTypePageResult of(
            List<CommonCodeTypeResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CommonCodeTypePageResult(results, pageMeta);
    }

    public static CommonCodeTypePageResult empty(int size) {
        return new CommonCodeTypePageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
