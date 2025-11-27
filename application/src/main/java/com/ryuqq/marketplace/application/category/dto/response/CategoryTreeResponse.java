package com.ryuqq.marketplace.application.category.dto.response;

import java.util.Collections;
import java.util.List;

public record CategoryTreeResponse(
    List<CategoryTreeNode> roots,
    int totalCount
) {
    public CategoryTreeResponse {
        roots = roots != null ? List.copyOf(roots) : Collections.emptyList();
    }

    public static CategoryTreeResponse empty() {
        return new CategoryTreeResponse(Collections.emptyList(), 0);
    }

    public static CategoryTreeResponse of(List<CategoryTreeNode> roots, int totalCount) {
        return new CategoryTreeResponse(roots, totalCount);
    }

    public boolean isEmpty() {
        return roots.isEmpty();
    }
}
