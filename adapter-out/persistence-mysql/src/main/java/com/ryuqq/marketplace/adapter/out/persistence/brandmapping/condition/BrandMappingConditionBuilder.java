package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.QBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandMapping QueryDSL 조건 빌더. */
@Component
public class BrandMappingConditionBuilder {

    private static final QBrandMappingJpaEntity brandMapping =
            QBrandMappingJpaEntity.brandMappingJpaEntity;

    private static final QSalesChannelBrandJpaEntity salesChannelBrand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;

    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? brandMapping.id.eq(id) : null;
    }

    public BooleanExpression salesChannelBrandIdsIn(BrandMappingSearchCriteria criteria) {
        if (!criteria.hasSalesChannelBrandFilter()) {
            return null;
        }
        return brandMapping.salesChannelBrandId.in(criteria.salesChannelBrandIds());
    }

    public BooleanExpression internalBrandIdsIn(BrandMappingSearchCriteria criteria) {
        if (!criteria.hasInternalBrandFilter()) {
            return null;
        }
        return brandMapping.internalBrandId.in(criteria.internalBrandIds());
    }

    public BooleanExpression salesChannelIdsIn(BrandMappingSearchCriteria criteria) {
        if (!criteria.hasSalesChannelFilter()) {
            return null;
        }
        return salesChannelBrand.salesChannelId.in(criteria.salesChannelIds());
    }

    public BooleanExpression statusIn(BrandMappingSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(BrandMappingStatus::name).toList();
        return brandMapping.status.in(statusNames);
    }

    public BooleanExpression searchCondition(BrandMappingSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return salesChannelBrand.externalBrandName.like(word).or(brand.nameKo.like(word));
        }
        return switch (criteria.searchField()) {
            case EXTERNAL_BRAND_NAME -> salesChannelBrand.externalBrandName.like(word);
            case INTERNAL_BRAND_NAME -> brand.nameKo.like(word);
        };
    }

    public boolean needsSalesChannelBrandJoin(BrandMappingSearchCriteria criteria) {
        return criteria.hasSalesChannelFilter()
                || (criteria.hasSearchCondition()
                        && (!criteria.hasSearchField()
                                || criteria.searchField()
                                        == com.ryuqq.marketplace.domain.brandmapping.query
                                                .BrandMappingSearchField.EXTERNAL_BRAND_NAME));
    }

    public boolean needsBrandJoin(BrandMappingSearchCriteria criteria) {
        return criteria.hasSearchCondition()
                && (!criteria.hasSearchField()
                        || criteria.searchField()
                                == com.ryuqq.marketplace.domain.brandmapping.query
                                        .BrandMappingSearchField.INTERNAL_BRAND_NAME);
    }
}
