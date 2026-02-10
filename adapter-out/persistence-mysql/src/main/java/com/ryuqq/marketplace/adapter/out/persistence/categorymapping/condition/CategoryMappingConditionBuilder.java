package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.QCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.QCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryMapping QueryDSL 조건 빌더. */
@Component
public class CategoryMappingConditionBuilder {

    private static final QCategoryMappingJpaEntity categoryMapping =
            QCategoryMappingJpaEntity.categoryMappingJpaEntity;

    private static final QSalesChannelCategoryJpaEntity salesChannelCategory =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;

    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? categoryMapping.id.eq(id) : null;
    }

    public BooleanExpression salesChannelCategoryIdsIn(CategoryMappingSearchCriteria criteria) {
        if (!criteria.hasSalesChannelCategoryFilter()) {
            return null;
        }
        return categoryMapping.salesChannelCategoryId.in(criteria.salesChannelCategoryIds());
    }

    public BooleanExpression internalCategoryIdsIn(CategoryMappingSearchCriteria criteria) {
        if (!criteria.hasInternalCategoryFilter()) {
            return null;
        }
        return categoryMapping.internalCategoryId.in(criteria.internalCategoryIds());
    }

    public BooleanExpression salesChannelIdsIn(CategoryMappingSearchCriteria criteria) {
        if (!criteria.hasSalesChannelFilter()) {
            return null;
        }
        return salesChannelCategory.salesChannelId.in(criteria.salesChannelIds());
    }

    public BooleanExpression statusIn(CategoryMappingSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(CategoryMappingStatus::name).toList();
        return categoryMapping.status.in(statusNames);
    }

    public BooleanExpression searchCondition(CategoryMappingSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return salesChannelCategory
                    .externalCategoryName
                    .like(word)
                    .or(category.nameKo.like(word));
        }
        return switch (criteria.searchField()) {
            case EXTERNAL_CATEGORY_NAME -> salesChannelCategory.externalCategoryName.like(word);
            case INTERNAL_CATEGORY_NAME -> category.nameKo.like(word);
        };
    }

    public boolean needsSalesChannelCategoryJoin(CategoryMappingSearchCriteria criteria) {
        return criteria.hasSalesChannelFilter()
                || (criteria.hasSearchCondition()
                        && (!criteria.hasSearchField()
                                || criteria.searchField()
                                        == com.ryuqq.marketplace.domain.categorymapping.query
                                                .CategoryMappingSearchField
                                                .EXTERNAL_CATEGORY_NAME));
    }

    public boolean needsCategoryJoin(CategoryMappingSearchCriteria criteria) {
        return criteria.hasSearchCondition()
                && (!criteria.hasSearchField()
                        || criteria.searchField()
                                == com.ryuqq.marketplace.domain.categorymapping.query
                                        .CategoryMappingSearchField.INTERNAL_CATEGORY_NAME);
    }
}
