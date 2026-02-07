package com.ryuqq.marketplace.application.commoncode.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 공통 코드 페이징 조회 결과 DTO.
 *
 * <p>페이징 조회 결과와 메타 정보를 함께 반환합니다.
 *
 * @param results 조회 결과 목록
 * @param pageMeta 페이징 메타 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodePageResult(List<CommonCodeResult> results, PageMeta pageMeta) {

    public static CommonCodePageResult of(List<CommonCodeResult> results, PageMeta pageMeta) {
        return new CommonCodePageResult(results, pageMeta);
    }

    public static CommonCodePageResult of(
            List<CommonCodeResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CommonCodePageResult(results, pageMeta);
    }

    public static CommonCodePageResult empty(int size) {
        return new CommonCodePageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
