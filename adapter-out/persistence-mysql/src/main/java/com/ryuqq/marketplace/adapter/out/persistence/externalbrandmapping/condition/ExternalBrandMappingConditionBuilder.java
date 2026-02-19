package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.QExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping QueryDSL 조건 빌더. */
@Component
public class ExternalBrandMappingConditionBuilder {

    private static final QExternalBrandMappingJpaEntity externalBrandMapping =
            QExternalBrandMappingJpaEntity.externalBrandMappingJpaEntity;

    public BooleanExpression externalSourceIdEq(ExternalBrandMappingSearchParams params) {
        Long externalSourceId = params.externalSourceId();
        if (externalSourceId == null) {
            return null;
        }
        return externalBrandMapping.externalSourceId.eq(externalSourceId);
    }

    public BooleanExpression statusIn(ExternalBrandMappingSearchParams params) {
        List<String> statuses = params.statuses();
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalBrandMapping.status.in(statuses);
    }

    public BooleanExpression searchCondition(ExternalBrandMappingSearchParams params) {
        String searchWord = params.searchWord();
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        String word = "%" + searchWord + "%";
        return externalBrandMapping
                .externalBrandName
                .like(word)
                .or(externalBrandMapping.externalBrandCode.like(word));
    }
}
