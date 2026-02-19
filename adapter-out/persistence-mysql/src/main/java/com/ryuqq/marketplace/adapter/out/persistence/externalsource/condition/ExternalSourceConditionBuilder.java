package com.ryuqq.marketplace.adapter.out.persistence.externalsource.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.QExternalSourceJpaEntity;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalSource QueryDSL 조건 빌더. */
@Component
public class ExternalSourceConditionBuilder {

    private static final QExternalSourceJpaEntity externalSource =
            QExternalSourceJpaEntity.externalSourceJpaEntity;

    public BooleanExpression typeIn(ExternalSourceSearchParams params) {
        List<String> types = params.types();
        if (types == null || types.isEmpty()) {
            return null;
        }
        return externalSource.type.in(types);
    }

    public BooleanExpression statusIn(ExternalSourceSearchParams params) {
        List<String> statuses = params.statuses();
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalSource.status.in(statuses);
    }

    public BooleanExpression searchCondition(ExternalSourceSearchParams params) {
        String searchWord = params.searchWord();
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        String word = "%" + searchWord + "%";
        return externalSource.name.like(word).or(externalSource.code.like(word));
    }
}
