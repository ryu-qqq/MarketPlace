package com.ryuqq.marketplace.application.saleschannelcategory.dto.response;

import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import java.time.Instant;

/** 외부 채널 카테고리 조회 결과 DTO. */
public record SalesChannelCategoryResult(
        Long id,
        Long salesChannelId,
        String externalCategoryCode,
        String externalCategoryName,
        Long parentId,
        int depth,
        String path,
        int sortOrder,
        boolean leaf,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static SalesChannelCategoryResult from(SalesChannelCategory category) {
        return new SalesChannelCategoryResult(
                category.idValue(),
                category.salesChannelId(),
                category.externalCategoryCode(),
                category.externalCategoryName(),
                category.parentId(),
                category.depth(),
                category.path(),
                category.sortOrder(),
                category.isLeaf(),
                category.status().name(),
                category.createdAt(),
                category.updatedAt());
    }
}
