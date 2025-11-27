package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

import java.util.List;

/**
 * 카테고리 경로 응답 DTO
 *
 * <p>Breadcrumb 형태의 카테고리 경로를 반환하는 API 응답 DTO입니다.</p>
 *
 * <p>예: 패션 &gt; 남성의류 &gt; 상의 &gt; 티셔츠</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record CategoryPathApiResponse(
    Long categoryId,
    List<CategoryApiResponse> ancestors
) {
    /**
     * 경로 깊이 반환
     *
     * @return 조상 카테고리 수
     */
    public int depth() {
        return ancestors != null ? ancestors.size() : 0;
    }
}
