package com.ryuqq.marketplace.domain.ordermapping.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.id.ExternalOrderItemMappingId;
import java.time.Instant;

/** 외부몰 상품주문번호와 내부 OrderItem ID 간의 매핑 Aggregate. */
public class ExternalOrderItemMapping {

    private final ExternalOrderItemMappingId id;
    private final long salesChannelId;
    private final String channelCode;
    private final String externalOrderId;
    private final String externalProductOrderId;
    private final OrderItemId orderItemId;
    private final Instant createdAt;

    private ExternalOrderItemMapping(
            ExternalOrderItemMappingId id,
            long salesChannelId,
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            OrderItemId orderItemId,
            Instant createdAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.channelCode = channelCode;
        this.externalOrderId = externalOrderId;
        this.externalProductOrderId = externalProductOrderId;
        this.orderItemId = orderItemId;
        this.createdAt = createdAt;
    }

    public static ExternalOrderItemMapping forNew(
            long salesChannelId,
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            OrderItemId orderItemId,
            Instant now) {
        validate(channelCode, externalOrderId, externalProductOrderId, orderItemId);
        return new ExternalOrderItemMapping(
                ExternalOrderItemMappingId.forNew(),
                salesChannelId,
                channelCode,
                externalOrderId,
                externalProductOrderId,
                orderItemId,
                now);
    }

    public static ExternalOrderItemMapping reconstitute(
            ExternalOrderItemMappingId id,
            long salesChannelId,
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            OrderItemId orderItemId,
            Instant createdAt) {
        return new ExternalOrderItemMapping(
                id,
                salesChannelId,
                channelCode,
                externalOrderId,
                externalProductOrderId,
                orderItemId,
                createdAt);
    }

    private static void validate(
            String channelCode,
            String externalOrderId,
            String externalProductOrderId,
            OrderItemId orderItemId) {
        if (channelCode == null || channelCode.isBlank()) {
            throw new IllegalArgumentException("channelCode는 null 또는 빈 문자열일 수 없습니다");
        }
        if (externalOrderId == null || externalOrderId.isBlank()) {
            throw new IllegalArgumentException("externalOrderId는 null 또는 빈 문자열일 수 없습니다");
        }
        if (externalProductOrderId == null || externalProductOrderId.isBlank()) {
            throw new IllegalArgumentException("externalProductOrderId는 null 또는 빈 문자열일 수 없습니다");
        }
        if (orderItemId == null) {
            throw new IllegalArgumentException("orderItemId는 null일 수 없습니다");
        }
    }

    public ExternalOrderItemMappingId id() {
        return id;
    }

    public long idValue() {
        return id.value();
    }

    public long salesChannelId() {
        return salesChannelId;
    }

    public String channelCode() {
        return channelCode;
    }

    public String externalOrderId() {
        return externalOrderId;
    }

    public String externalProductOrderId() {
        return externalProductOrderId;
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public String orderItemIdValue() {
        return orderItemId.value();
    }

    public Instant createdAt() {
        return createdAt;
    }
}
