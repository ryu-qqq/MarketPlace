package com.ryuqq.marketplace.application.category.dto.response;

import java.util.Collections;
import java.util.List;

public record CategoryPathResponse(
    Long categoryId,
    List<CategoryResponse> ancestors
) {
    public CategoryPathResponse {
        ancestors = ancestors != null ? List.copyOf(ancestors) : Collections.emptyList();
    }

    public static CategoryPathResponse of(Long categoryId, List<CategoryResponse> ancestors) {
        return new CategoryPathResponse(categoryId, ancestors);
    }

    public int depth() {
        return ancestors.size();
    }

    public CategoryResponse current() {
        return ancestors.isEmpty() ? null : ancestors.get(ancestors.size() - 1);
    }

    public CategoryResponse root() {
        return ancestors.isEmpty() ? null : ancestors.get(0);
    }
}
