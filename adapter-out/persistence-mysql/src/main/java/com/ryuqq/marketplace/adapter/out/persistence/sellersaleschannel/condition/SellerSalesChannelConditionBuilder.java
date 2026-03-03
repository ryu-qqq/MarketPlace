package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.QSellerSalesChannelJpaEntity.sellerSalesChannelJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import org.springframework.stereotype.Component;

/**
 * SellerSalesChannelConditionBuilder - 셀러 판매채널 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerSalesChannelConditionBuilder {

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerSalesChannelJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression salesChannelIdEq(Long salesChannelId) {
        return salesChannelId != null
                ? sellerSalesChannelJpaEntity.salesChannelId.eq(salesChannelId)
                : null;
    }

    public BooleanExpression connectionStatusConnected() {
        return sellerSalesChannelJpaEntity.connectionStatus.eq(
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED);
    }

    public BooleanExpression channelCodeEq(String channelCode) {
        return channelCode != null ? sellerSalesChannelJpaEntity.channelCode.eq(channelCode) : null;
    }
}
