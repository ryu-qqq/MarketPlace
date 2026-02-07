package com.ryuqq.marketplace.application.category.dto.response;

import com.ryuqq.marketplace.domain.category.aggregate.Category;
import java.time.Instant;

/** 카테고리 조회 결과 DTO. */
public record CategoryResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        Long parentId,
        int depth,
        String path,
        int sortOrder,
        boolean leaf,
        String status,
        String department,
        String categoryGroup,
        Instant createdAt,
        Instant updatedAt) {

    public static CategoryResult from(Category category) {
        return new CategoryResult(
                category.idValue(),
                category.codeValue(),
                category.nameKo(),
                category.nameEn(),
                category.parentId(),
                category.depthValue(),
                category.pathValue(),
                category.sortOrderValue(),
                category.isLeaf(),
                category.status().name(),
                category.department().name(),
                category.categoryGroup().name(),
                category.createdAt(),
                category.updatedAt());
    }
}
