package com.ryuqq.marketplace.application.common.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * PagedResult - 페이징 결과 응답
 *
 * <p>페이징 조회 결과와 메타 정보를 함께 반환합니다.
 *
 * @param content 조회 결과 목록
 * @param pageMeta 페이징 메타 정보
 * @param <T> 결과 타입
 * @author ryu-qqq
 */
public record PagedResult<T>(List<T> content, PageMeta pageMeta) {

    public static <T> PagedResult<T> of(List<T> content, PageMeta pageMeta) {
        return new PagedResult<>(content, pageMeta);
    }

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new PagedResult<>(content, pageMeta);
    }

    public static <T> PagedResult<T> empty(int size) {
        return new PagedResult<>(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    public int size() {
        return content != null ? content.size() : 0;
    }
}
