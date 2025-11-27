package com.ryuqq.marketplace.application.category.dto.response;

public record CategoryResponse(
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
) {
    public boolean isRoot() {
        return parentId == null;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
