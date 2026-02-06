package com.ryuqq.marketplace.adapter.out.persistence.category.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import org.springframework.stereotype.Component;

/** Category JPA Entity Mapper. */
@Component
public class CategoryJpaEntityMapper {

    public CategoryJpaEntity toEntity(Category category) {
        return CategoryJpaEntity.create(
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
                category.updatedAt(),
                category.deletedAt());
    }

    public Category toDomain(CategoryJpaEntity entity) {
        var id = entity.getId() != null ? CategoryId.of(entity.getId()) : CategoryId.forNew();
        return Category.reconstitute(
                id,
                CategoryCode.of(entity.getCode()),
                CategoryName.of(entity.getNameKo(), entity.getNameEn()),
                entity.getParentId(),
                CategoryDepth.of(entity.getDepth()),
                CategoryPath.of(entity.getPath()),
                SortOrder.of(entity.getSortOrder()),
                entity.isLeaf(),
                CategoryStatus.fromString(entity.getStatus()),
                Department.fromString(entity.getDepartment()),
                CategoryGroup.fromString(entity.getCategoryGroup()),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
