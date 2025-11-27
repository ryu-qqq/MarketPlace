package com.ryuqq.marketplace.adapter.in.rest.category.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 카테고리 이동 요청 DTO
 *
 * <p>카테고리를 다른 부모 아래로 이동할 때 필요한 정보를 담는 Request DTO입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record MoveCategoryApiRequest(
    @NotNull(message = "새 부모 카테고리 ID는 필수입니다")
    Long newParentId,

    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    Integer newSortOrder
) {}
