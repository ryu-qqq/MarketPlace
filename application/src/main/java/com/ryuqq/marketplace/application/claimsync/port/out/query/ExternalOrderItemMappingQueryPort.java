package com.ryuqq.marketplace.application.claimsync.port.out.query;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;

import java.util.List;
import java.util.Optional;

/** 외부 주문상품 매핑 조회 포트. */
public interface ExternalOrderItemMappingQueryPort {

    Optional<ExternalOrderItemMapping> findBySalesChannelIdAndExternalProductOrderId(
            long salesChannelId, String externalProductOrderId);

    List<ExternalOrderItemMapping> findByOrderItemIds(List<OrderItemId> orderItemIds);
}
