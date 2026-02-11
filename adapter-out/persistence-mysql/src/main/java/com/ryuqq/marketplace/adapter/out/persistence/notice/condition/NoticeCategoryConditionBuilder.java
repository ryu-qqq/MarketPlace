package com.ryuqq.marketplace.adapter.out.persistence.notice.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.QNoticeCategoryJpaEntity;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import org.springframework.stereotype.Component;

/** NoticeCategory QueryDSL 조건 빌더. */
@Component
public class NoticeCategoryConditionBuilder {

    private static final QNoticeCategoryJpaEntity noticeCategory =
            QNoticeCategoryJpaEntity.noticeCategoryJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? noticeCategory.id.eq(id) : null;
    }

    public BooleanExpression targetCategoryGroupEq(String targetCategoryGroup) {
        return targetCategoryGroup != null
                ? noticeCategory.targetCategoryGroup.eq(targetCategoryGroup)
                : null;
    }

    public BooleanExpression activeEq(NoticeCategorySearchCriteria criteria) {
        if (!criteria.hasActiveFilter()) {
            return null;
        }
        return noticeCategory.active.eq(criteria.active());
    }

    public BooleanExpression searchCondition(NoticeCategorySearchCriteria criteria) {
        if (!criteria.hasSearchFilter()) {
            return null;
        }
        String field = criteria.searchField();
        String word = criteria.searchWord();

        return switch (field) {
            case "CODE" -> noticeCategory.code.containsIgnoreCase(word);
            case "NAME_KO" -> noticeCategory.nameKo.containsIgnoreCase(word);
            case "NAME_EN" -> noticeCategory.nameEn.containsIgnoreCase(word);
            default -> null;
        };
    }
}
