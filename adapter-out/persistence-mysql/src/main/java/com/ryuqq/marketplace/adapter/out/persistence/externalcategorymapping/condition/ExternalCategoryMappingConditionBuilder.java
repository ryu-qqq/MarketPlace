package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.QExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping QueryDSL 조건 빌더. */
@Component
public class ExternalCategoryMappingConditionBuilder {

    private static final QExternalCategoryMappingJpaEntity externalCategoryMapping =
            QExternalCategoryMappingJpaEntity.externalCategoryMappingJpaEntity;

    public BooleanExpression externalSourceIdEq(ExternalCategoryMappingSearchParams params) {
        Long externalSourceId = params.externalSourceId();
        if (externalSourceId == null) {
            return null;
        }
        return externalCategoryMapping.externalSourceId.eq(externalSourceId);
    }

    public BooleanExpression statusIn(ExternalCategoryMappingSearchParams params) {
        List<String> statuses = params.statuses();
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalCategoryMapping.status.in(statuses);
    }

    public BooleanExpression searchCondition(ExternalCategoryMappingSearchParams params) {
        String searchWord = params.searchWord();
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        String word = "%" + searchWord + "%";
        return externalCategoryMapping
                .externalCategoryName
                .like(word)
                .or(externalCategoryMapping.externalCategoryCode.like(word));
    }
}
