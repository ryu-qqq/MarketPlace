package com.ryuqq.marketplace.adapter.out.persistence.externalsource.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.QExternalSourceJpaEntity;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalSource QueryDSL 조건 빌더. */
@Component
public class ExternalSourceConditionBuilder {

    private static final QExternalSourceJpaEntity externalSource =
            QExternalSourceJpaEntity.externalSourceJpaEntity;

    public BooleanExpression typeIn(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return externalSource.type.in(types);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalSource.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            ExternalSourceSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return externalSource
                    .name
                    .containsIgnoreCase(searchWord)
                    .or(externalSource.code.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case CODE -> externalSource.code.containsIgnoreCase(searchWord);
            case NAME -> externalSource.name.containsIgnoreCase(searchWord);
        };
    }
}
