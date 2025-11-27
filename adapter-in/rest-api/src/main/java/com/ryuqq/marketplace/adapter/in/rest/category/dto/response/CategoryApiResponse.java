package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

/**
 * 카테고리 기본 정보 응답 DTO
 *
 * <p>RESTful API 응답으로 사용되는 카테고리 기본 정보 record입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record CategoryApiResponse(
    Long id,
    String code,
    String nameKo,
    String nameEn,
    Long parentId,
    int depth,
    String path,
    int sortOrder,
    boolean isLeaf,
    String status,
    boolean isVisible,
    boolean isListable,
    String department,
    String productGroup,
    String genderScope,
    String ageGroup,
    String displayName,
    String seoSlug,
    String iconUrl
) {}
