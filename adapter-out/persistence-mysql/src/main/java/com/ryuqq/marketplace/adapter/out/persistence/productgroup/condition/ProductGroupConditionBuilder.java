package com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** ProductGroup QueryDSL 조건 빌더. */
@Component
public class ProductGroupConditionBuilder {

    private static final QProductGroupJpaEntity productGroup =
            QProductGroupJpaEntity.productGroupJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? productGroup.id.eq(id) : null;
    }

    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? productGroup.id.in(ids) : null;
    }

    public BooleanExpression sellerIdIn(List<Long> sellerIds) {
        return sellerIds != null && !sellerIds.isEmpty()
                ? productGroup.sellerId.in(sellerIds)
                : null;
    }

    public BooleanExpression brandIdIn(List<Long> brandIds) {
        return brandIds != null && !brandIds.isEmpty() ? productGroup.brandId.in(brandIds) : null;
    }

    public BooleanExpression categoryIdIn(List<Long> categoryIds) {
        return categoryIds != null && !categoryIds.isEmpty()
                ? productGroup.categoryId.in(categoryIds)
                : null;
    }

    public BooleanExpression statusIn(ProductGroupSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(ProductGroupStatus::name).toList();
        return productGroup.status.in(statusNames);
    }

    public BooleanExpression statusNotDeleted() {
        return productGroup.status.ne(ProductGroupStatus.DELETED.name());
    }

    public BooleanExpression createdAtGoe(ProductGroupSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        DateRange dateRange = criteria.dateRange();
        return dateRange.startInstant() != null
                ? productGroup.createdAt.goe(dateRange.startInstant())
                : null;
    }

    public BooleanExpression createdAtLoe(ProductGroupSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        DateRange dateRange = criteria.dateRange();
        return dateRange.endInstant() != null
                ? productGroup.createdAt.loe(dateRange.endInstant())
                : null;
    }

    public BooleanExpression searchCondition(ProductGroupSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        if (!criteria.hasSearchField()) {
            return productGroup.productGroupName.containsIgnoreCase(criteria.searchWord());
        }
        return switch (criteria.searchField()) {
            case NAME -> productGroup.productGroupName.containsIgnoreCase(criteria.searchWord());
            case CATEGORY_NAME, BRAND_NAME -> null;
        };
    }
}
