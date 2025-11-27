package com.ryuqq.marketplace.application.category.dto.command;

public record CreateCategoryCommand(
    Long parentId, // nullable for root
    String code,
    String nameKo,
    String nameEn,
    Integer sortOrder,
    Boolean isListable,
    Boolean isVisible,
    String department,
    String productGroup,
    String genderScope,
    String ageGroup,
    String displayName,
    String seoSlug,
    String iconUrl
) {
    public CreateCategoryCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (nameKo == null || nameKo.isBlank()) {
            throw new IllegalArgumentException("nameKo is required");
        }
    }

    public boolean isRootCategory() {
        return parentId == null;
    }
}
