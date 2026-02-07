package com.ryuqq.marketplace.adapter.out.persistence.brand.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** Brand QueryDSL 조건 빌더. */
@Component
public class BrandConditionBuilder {

    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? brand.id.eq(id) : null;
    }

    public BooleanExpression statusIn(BrandSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(BrandStatus::name).toList();
        return brand.status.in(statusNames);
    }

    public BooleanExpression searchCondition(BrandSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return brand.nameKo.like(word).or(brand.nameEn.like(word)).or(brand.code.like(word));
        }
        return switch (criteria.searchField()) {
            case CODE -> brand.code.like(word);
            case NAME_KO -> brand.nameKo.like(word);
            case NAME_EN -> brand.nameEn.like(word);
        };
    }

    public BooleanExpression notDeleted() {
        return brand.deletedAt.isNull();
    }
}
