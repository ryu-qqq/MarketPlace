package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.QExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping QueryDSL 조건 빌더. */
@Component
public class ExternalCategoryMappingConditionBuilder {

    private static final QExternalCategoryMappingJpaEntity externalCategoryMapping =
            QExternalCategoryMappingJpaEntity.externalCategoryMappingJpaEntity;

    public BooleanExpression externalSourceIdEq(Long externalSourceId) {
        if (externalSourceId == null) {
            return null;
        }
        return externalCategoryMapping.externalSourceId.eq(externalSourceId);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return externalCategoryMapping.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            ExternalCategoryMappingSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return externalCategoryMapping
                    .externalCategoryName
                    .containsIgnoreCase(searchWord)
                    .or(
                            externalCategoryMapping.externalCategoryCode.containsIgnoreCase(
                                    searchWord));
        }
        return switch (searchField) {
            case EXTERNAL_CODE ->
                    externalCategoryMapping.externalCategoryCode.containsIgnoreCase(searchWord);
            case EXTERNAL_NAME ->
                    externalCategoryMapping.externalCategoryName.containsIgnoreCase(searchWord);
        };
    }
}
