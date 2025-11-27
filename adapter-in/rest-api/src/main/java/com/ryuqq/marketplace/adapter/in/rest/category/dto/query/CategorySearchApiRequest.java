package com.ryuqq.marketplace.adapter.in.rest.category.dto.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 카테고리 검색 요청 DTO
 *
 * <p>카테고리 검색 시 필요한 조건들을 담는 Request DTO입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record CategorySearchApiRequest(
    String keyword,

    String status,

    String department,

    String productGroup,

    Boolean isLeaf,

    Boolean isVisible,

    Boolean isListable,

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    Integer page,

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    Integer size
) {
    public CategorySearchApiRequest {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
    }
}
