package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelCategory QueryDSL 조건 빌더. */
@Component
public class SalesChannelCategoryConditionBuilder {

    private static final QSalesChannelCategoryJpaEntity category =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? category.id.eq(id) : null;
    }

    public BooleanExpression salesChannelIdEq(Long salesChannelId) {
        return salesChannelId != null ? category.salesChannelId.eq(salesChannelId) : null;
    }

    public BooleanExpression salesChannelIdsIn(Collection<Long> salesChannelIds) {
        return salesChannelIds != null && !salesChannelIds.isEmpty()
                ? category.salesChannelId.in(salesChannelIds)
                : null;
    }

    public BooleanExpression externalCategoryCodeEq(String externalCategoryCode) {
        return externalCategoryCode != null
                ? category.externalCategoryCode.eq(externalCategoryCode)
                : null;
    }

    public BooleanExpression statusIn(SalesChannelCategorySearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(SalesChannelCategoryStatus::name).toList();
        return category.status.in(statusNames);
    }

    public BooleanExpression searchCondition(SalesChannelCategorySearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return category.externalCategoryName
                    .like(word)
                    .or(category.externalCategoryCode.like(word));
        }
        return switch (criteria.searchField()) {
            case EXTERNAL_CODE -> category.externalCategoryCode.like(word);
            case EXTERNAL_NAME -> category.externalCategoryName.like(word);
        };
    }
}
