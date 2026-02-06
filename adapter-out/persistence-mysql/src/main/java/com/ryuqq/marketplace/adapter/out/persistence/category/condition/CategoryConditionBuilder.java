package com.ryuqq.marketplace.adapter.out.persistence.category.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import java.util.List;
import org.springframework.stereotype.Component;

/** Category QueryDSL 조건 빌더. */
@Component
public class CategoryConditionBuilder {

    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? category.id.eq(id) : null;
    }

    public BooleanExpression parentIdEq(CategorySearchCriteria criteria) {
        if (!criteria.hasParentFilter()) {
            return null;
        }
        return category.parentId.eq(criteria.parentId());
    }

    public BooleanExpression depthEq(CategorySearchCriteria criteria) {
        if (!criteria.hasDepthFilter()) {
            return null;
        }
        return category.depth.eq(criteria.depth());
    }

    public BooleanExpression leafEq(CategorySearchCriteria criteria) {
        if (!criteria.hasLeafFilter()) {
            return null;
        }
        return category.leaf.eq(criteria.leaf());
    }

    public BooleanExpression statusIn(CategorySearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(CategoryStatus::name).toList();
        return category.status.in(statusNames);
    }

    public BooleanExpression departmentIn(CategorySearchCriteria criteria) {
        if (!criteria.hasDepartmentFilter()) {
            return null;
        }
        List<String> departmentNames =
                criteria.departments().stream().map(Department::name).toList();
        return category.department.in(departmentNames);
    }

    public BooleanExpression categoryGroupIn(CategorySearchCriteria criteria) {
        if (!criteria.hasCategoryGroupFilter()) {
            return null;
        }
        List<String> categoryGroupNames =
                criteria.categoryGroups().stream().map(CategoryGroup::name).toList();
        return category.categoryGroup.in(categoryGroupNames);
    }

    public BooleanExpression searchCondition(CategorySearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return category.nameKo
                    .like(word)
                    .or(category.nameEn.like(word))
                    .or(category.code.like(word));
        }
        return switch (criteria.searchField()) {
            case CODE -> category.code.like(word);
            case NAME_KO -> category.nameKo.like(word);
            case NAME_EN -> category.nameEn.like(word);
        };
    }

    public BooleanExpression notDeleted() {
        return category.deletedAt.isNull();
    }
}
