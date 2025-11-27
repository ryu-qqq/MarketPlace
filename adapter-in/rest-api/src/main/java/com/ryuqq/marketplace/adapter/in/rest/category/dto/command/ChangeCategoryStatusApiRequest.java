package com.ryuqq.marketplace.adapter.in.rest.category.dto.command;

import jakarta.validation.constraints.NotNull;

/**
 * 카테고리 상태 변경 요청 DTO
 *
 * <p>카테고리 상태 변경 시 필요한 정보를 담는 Request DTO입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record ChangeCategoryStatusApiRequest(
    @NotNull(message = "상태는 필수입니다")
    String status,

    Long replacementCategoryId
) {}
