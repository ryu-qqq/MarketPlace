package com.ryuqq.marketplace.application.category.dto.command;

public record MoveCategoryCommand(
    Long categoryId,
    Long newParentId,
    Integer newSortOrder
) {
    public MoveCategoryCommand {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
    }

    public boolean isMovingToRoot() {
        return newParentId == null;
    }
}
