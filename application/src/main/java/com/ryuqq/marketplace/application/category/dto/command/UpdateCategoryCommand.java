package com.ryuqq.marketplace.application.category.dto.command;

public record UpdateCategoryCommand(
    Long categoryId,
    String nameKo,
    String nameEn,
    Boolean isListable,
    Boolean isVisible,
    Integer sortOrder,
    String displayName,
    String seoSlug,
    String iconUrl
) {
    public UpdateCategoryCommand {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
    }
}
