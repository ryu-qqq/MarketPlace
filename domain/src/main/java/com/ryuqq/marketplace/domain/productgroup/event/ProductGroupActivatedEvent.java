package com.ryuqq.marketplace.domain.productgroup.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * 상품그룹 활성화 이벤트.
 *
 * <p>검수 통과 후 상품그룹이 ACTIVE 상태로 전환되었을 때 발행됩니다.
 *
 * @param productGroupId 상품그룹 ID
 * @param sellerId 셀러 ID
 * @param occurredAt 이벤트 발생 시각
 */
public record ProductGroupActivatedEvent(
        ProductGroupId productGroupId, SellerId sellerId, Instant occurredAt)
        implements DomainEvent {

    public static ProductGroupActivatedEvent of(
            ProductGroupId productGroupId, SellerId sellerId, Instant now) {
        return new ProductGroupActivatedEvent(productGroupId, sellerId, now);
    }
}
