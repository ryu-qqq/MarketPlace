package com.ryuqq.marketplace.domain.inboundproduct.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;

/** InboundProduct가 ProductGroup으로 변환 완료된 이벤트. */
public record InboundProductConvertedEvent(
        InboundProductId inboundProductId,
        ProductGroupId productGroupId,
        boolean isNewRegistration,
        Instant occurredAt)
        implements DomainEvent {}
