package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.QExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping QueryDSL 조건 빌더. */
@Component
public class ExternalBrandMappingConditionBuilder {

    private static final QExternalBrandMappingJpaEntity externalBrandMapping =
            QExternalBrandMappingJpaEntity.externalBrandMappingJpaEntity;

    public BooleanExpression externalSourceIdEq(Long externalSourceId) {
        if (externalSourceId == null) {
            return null;
        }
        return externalBrandMapping.externalSourceId.eq(externalSourceId);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalBrandMapping.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            ExternalBrandMappingSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return externalBrandMapping
                    .externalBrandName
                    .containsIgnoreCase(searchWord)
                    .or(externalBrandMapping.externalBrandCode.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case EXTERNAL_CODE ->
                    externalBrandMapping.externalBrandCode.containsIgnoreCase(searchWord);
            case EXTERNAL_NAME ->
                    externalBrandMapping.externalBrandName.containsIgnoreCase(searchWord);
        };
    }
}
