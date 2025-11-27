package com.ryuqq.marketplace.adapter.in.rest.category.dto.command;

import jakarta.validation.constraints.*;

/**
 * 카테고리 수정 요청 DTO
 *
 * <p>카테고리 수정 시 필요한 정보를 담는 Request DTO입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record UpdateCategoryApiRequest(
    @NotBlank(message = "한글 카테고리명은 필수입니다")
    @Size(max = 255, message = "한글 카테고리명은 255자 이내여야 합니다")
    String nameKo,

    @Size(max = 255, message = "영문 카테고리명은 255자 이내여야 합니다")
    String nameEn,

    Boolean isListable,

    Boolean isVisible,

    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    Integer sortOrder,

    String genderScope,

    String ageGroup,

    @Size(max = 255, message = "표시용 이름은 255자 이내여야 합니다")
    String displayName,

    @Size(max = 255, message = "SEO 슬러그는 255자 이내여야 합니다")
    String seoSlug,

    @Size(max = 500, message = "아이콘 URL은 500자 이내여야 합니다")
    String iconUrl
) {}
