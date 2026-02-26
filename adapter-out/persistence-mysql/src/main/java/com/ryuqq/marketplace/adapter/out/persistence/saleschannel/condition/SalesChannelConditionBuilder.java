package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.QSalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannel QueryDSL 조건 빌더. */
@Component
public class SalesChannelConditionBuilder {

    private static final QSalesChannelJpaEntity salesChannel =
            QSalesChannelJpaEntity.salesChannelJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? salesChannel.id.eq(id) : null;
    }

    public BooleanExpression channelNameEq(String channelName) {
        return channelName != null ? salesChannel.channelName.eq(channelName) : null;
    }

    public BooleanExpression statusIn(SalesChannelSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames =
                criteria.statuses().stream().map(SalesChannelStatus::name).toList();
        return salesChannel.status.in(statusNames);
    }

    public BooleanExpression searchCondition(SalesChannelSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return salesChannel.channelName.like(word);
        }
        return switch (criteria.searchField()) {
            case CHANNEL_NAME -> salesChannel.channelName.like(word);
        };
    }
}
