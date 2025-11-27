package com.ryuqq.marketplace.application.category.dto.command;

public record ChangeCategoryStatusCommand(
    Long categoryId,
    String newStatus,
    Long replacementCategoryId // for DEPRECATED, optional
) {
    public ChangeCategoryStatusCommand {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus is required");
        }
    }

    public boolean hasReplacement() {
        return replacementCategoryId != null;
    }
}
