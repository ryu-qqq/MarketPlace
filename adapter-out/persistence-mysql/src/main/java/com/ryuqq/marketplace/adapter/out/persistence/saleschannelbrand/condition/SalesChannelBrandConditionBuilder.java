package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelBrand QueryDSL 조건 빌더. */
@Component
public class SalesChannelBrandConditionBuilder {

    private static final QSalesChannelBrandJpaEntity brand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? brand.id.eq(id) : null;
    }

    public BooleanExpression salesChannelIdEq(Long salesChannelId) {
        return salesChannelId != null ? brand.salesChannelId.eq(salesChannelId) : null;
    }

    public BooleanExpression salesChannelIdsIn(Collection<Long> salesChannelIds) {
        return salesChannelIds != null && !salesChannelIds.isEmpty()
                ? brand.salesChannelId.in(salesChannelIds)
                : null;
    }

    public BooleanExpression externalBrandCodeEq(String externalBrandCode) {
        return externalBrandCode != null ? brand.externalBrandCode.eq(externalBrandCode) : null;
    }

    public BooleanExpression statusIn(SalesChannelBrandSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(SalesChannelBrandStatus::name).toList();
        return brand.status.in(statusNames);
    }

    public BooleanExpression searchCondition(SalesChannelBrandSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return brand.externalBrandName.like(word).or(brand.externalBrandCode.like(word));
        }
        return switch (criteria.searchField()) {
            case EXTERNAL_CODE -> brand.externalBrandCode.like(word);
            case EXTERNAL_NAME -> brand.externalBrandName.like(word);
        };
    }
}
