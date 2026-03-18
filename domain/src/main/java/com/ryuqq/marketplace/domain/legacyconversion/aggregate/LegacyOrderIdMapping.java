package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import java.time.Instant;

/**
 * 레거시 주문 ID 매핑 Aggregate.
 *
 * <p>레거시 주문 ID와 내부 주문 ID 간의 매핑을 저장합니다. 변환 완료 시 생성되며, 중복 이관 방지 및 역추적에 사용됩니다.
 */
public class LegacyOrderIdMapping {

    private final LegacyOrderIdMappingId id;
    private final long legacyOrderId;
    private final long legacyPaymentId;
    private final String internalOrderId;
    private final long salesChannelId;
    private final String channelName;
    private final Instant createdAt;

    private LegacyOrderIdMapping(
            LegacyOrderIdMappingId id,
            long legacyOrderId,
            long legacyPaymentId,
            String internalOrderId,
            long salesChannelId,
            String channelName,
            Instant createdAt) {
        this.id = id;
        this.legacyOrderId = legacyOrderId;
        this.legacyPaymentId = legacyPaymentId;
        this.internalOrderId = internalOrderId;
        this.salesChannelId = salesChannelId;
        this.channelName = channelName;
        this.createdAt = createdAt;
    }

    public static LegacyOrderIdMapping forNew(
            long legacyOrderId,
            long legacyPaymentId,
            String internalOrderId,
            long salesChannelId,
            String channelName,
            Instant now) {
        return new LegacyOrderIdMapping(
                LegacyOrderIdMappingId.forNew(),
                legacyOrderId,
                legacyPaymentId,
                internalOrderId,
                salesChannelId,
                channelName,
                now);
    }

    public static LegacyOrderIdMapping reconstitute(
            LegacyOrderIdMappingId id,
            long legacyOrderId,
            long legacyPaymentId,
            String internalOrderId,
            long salesChannelId,
            String channelName,
            Instant createdAt) {
        return new LegacyOrderIdMapping(
                id,
                legacyOrderId,
                legacyPaymentId,
                internalOrderId,
                salesChannelId,
                channelName,
                createdAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    // Getters
    public LegacyOrderIdMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long legacyOrderId() {
        return legacyOrderId;
    }

    public long legacyPaymentId() {
        return legacyPaymentId;
    }

    public String internalOrderId() {
        return internalOrderId;
    }

    public long salesChannelId() {
        return salesChannelId;
    }

    public String channelName() {
        return channelName;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
