package com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

/** 세토프 TreeCategoryContext 호환 응답 DTO. */
public record LegacyTreeCategoryContext(
        long categoryId,
        String categoryName,
        String displayName,
        int categoryDepth,
        long parentCategoryId,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<LegacyTreeCategoryContext> children) {

    public LegacyTreeCategoryContext(
            long categoryId,
            String categoryName,
            String displayName,
            int categoryDepth,
            long parentCategoryId) {
        this(
                categoryId,
                categoryName,
                displayName,
                categoryDepth,
                parentCategoryId,
                new ArrayList<>());
    }
}
