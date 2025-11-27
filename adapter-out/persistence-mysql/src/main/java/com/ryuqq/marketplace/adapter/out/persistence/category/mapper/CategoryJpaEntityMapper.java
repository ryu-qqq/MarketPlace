package com.ryuqq.marketplace.adapter.out.persistence.category.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;

import org.springframework.stereotype.Component;

/**
 * CategoryJpaEntityMapper - Category Entity ↔ Domain 변환
 */
@Component
public class CategoryJpaEntityMapper {

    public CategoryJpaEntity toEntity(Category category) {
        return CategoryJpaEntity.from(category);
    }

    public Category toDomain(CategoryJpaEntity entity) {
        return Category.reconstitute(
            CategoryId.of(entity.getId()),
            CategoryCode.of(entity.getCode()),
            CategoryName.of(entity.getNameKo(), entity.getNameEn()),
            entity.getParentId(),
            CategoryDepth.of(entity.getDepth()),
            entity.getPath() != null ? CategoryPath.of(entity.getPath()) : null,
            SortOrder.of(entity.getSortOrder()),
            entity.isLeaf(),
            entity.getStatus(),
            CategoryVisibility.of(entity.isVisible(), entity.isListable()),
            entity.getDepartment(),
            entity.getProductGroup(),
            entity.getGenderScope(),
            entity.getAgeGroup(),
            CategoryMeta.of(entity.getDisplayName(), entity.getSeoSlug(), entity.getIconUrl()),
            entity.getVersion()
        );
    }
}
