package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.QInboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundSource QueryDSL 조건 빌더. */
@Component
public class InboundSourceConditionBuilder {

    private static final QInboundSourceJpaEntity inboundSource =
            QInboundSourceJpaEntity.inboundSourceJpaEntity;

    public BooleanExpression typeIn(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return inboundSource.type.in(types);
    }

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return inboundSource.status.in(statuses);
    }

    /** 검색 필드별 검색 조건. searchField가 null이면 전체 필드 검색. */
    public BooleanExpression searchCondition(
            ExternalSourceSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return inboundSource
                    .name
                    .containsIgnoreCase(searchWord)
                    .or(inboundSource.code.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case CODE -> inboundSource.code.containsIgnoreCase(searchWord);
            case NAME -> inboundSource.name.containsIgnoreCase(searchWord);
        };
    }
}
