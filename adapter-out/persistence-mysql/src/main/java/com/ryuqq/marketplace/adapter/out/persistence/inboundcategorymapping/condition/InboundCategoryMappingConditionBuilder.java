package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.QInboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping QueryDSL 조건 빌더. */
@Component
public class InboundCategoryMappingConditionBuilder {

    private static final QInboundCategoryMappingJpaEntity inboundCategoryMapping =
            QInboundCategoryMappingJpaEntity.inboundCategoryMappingJpaEntity;

    public BooleanExpression inboundSourceIdEq(Long inboundSourceId) {
        if (inboundSourceId == null) {
            return null;
        }
        return inboundCategoryMapping.inboundSourceId.eq(inboundSourceId);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return inboundCategoryMapping.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            InboundCategoryMappingSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return inboundCategoryMapping
                    .externalCategoryName
                    .containsIgnoreCase(searchWord)
                    .or(inboundCategoryMapping.externalCategoryCode.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case EXTERNAL_CODE ->
                    inboundCategoryMapping.externalCategoryCode.containsIgnoreCase(searchWord);
            case EXTERNAL_NAME ->
                    inboundCategoryMapping.externalCategoryName.containsIgnoreCase(searchWord);
        };
    }
}
