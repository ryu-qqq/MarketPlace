package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.QCanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import org.springframework.stereotype.Component;

/** CanonicalOptionGroup QueryDSL 조건 빌더. */
@Component
public class CanonicalOptionGroupConditionBuilder {

    private static final QCanonicalOptionGroupJpaEntity canonicalOptionGroup =
            QCanonicalOptionGroupJpaEntity.canonicalOptionGroupJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? canonicalOptionGroup.id.eq(id) : null;
    }

    public BooleanExpression activeEq(CanonicalOptionGroupSearchCriteria criteria) {
        if (!criteria.hasActiveFilter()) {
            return null;
        }
        return canonicalOptionGroup.active.eq(criteria.active());
    }

    public BooleanExpression searchCondition(CanonicalOptionGroupSearchCriteria criteria) {
        if (!criteria.hasSearchFilter()) {
            return null;
        }
        String field = criteria.searchField();
        String word = criteria.searchWord();

        return switch (field) {
            case "CODE" -> canonicalOptionGroup.code.containsIgnoreCase(word);
            case "NAME_KO" -> canonicalOptionGroup.nameKo.containsIgnoreCase(word);
            case "NAME_EN" -> canonicalOptionGroup.nameEn.containsIgnoreCase(word);
            default -> null;
        };
    }
}
