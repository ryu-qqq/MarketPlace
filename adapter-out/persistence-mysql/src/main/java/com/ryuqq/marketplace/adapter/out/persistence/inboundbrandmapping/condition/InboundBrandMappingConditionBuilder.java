package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.QInboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundBrandMapping QueryDSL 조건 빌더. */
@Component
public class InboundBrandMappingConditionBuilder {

    private static final QInboundBrandMappingJpaEntity inboundBrandMapping =
            QInboundBrandMappingJpaEntity.inboundBrandMappingJpaEntity;

    public BooleanExpression inboundSourceIdEq(Long inboundSourceId) {
        if (inboundSourceId == null) {
            return null;
        }
        return inboundBrandMapping.inboundSourceId.eq(inboundSourceId);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return inboundBrandMapping.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            InboundBrandMappingSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return inboundBrandMapping
                    .externalBrandName
                    .containsIgnoreCase(searchWord)
                    .or(inboundBrandMapping.externalBrandCode.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case EXTERNAL_CODE ->
                    inboundBrandMapping.externalBrandCode.containsIgnoreCase(searchWord);
            case EXTERNAL_NAME ->
                    inboundBrandMapping.externalBrandName.containsIgnoreCase(searchWord);
        };
    }
}
