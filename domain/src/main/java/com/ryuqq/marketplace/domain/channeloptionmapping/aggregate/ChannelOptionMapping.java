package com.ryuqq.marketplace.domain.channeloptionmapping.aggregate;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;

/**
 * 채널 옵션 매핑 Aggregate Root. 캐노니컬 옵션 값을 외부몰별 옵션 코드에 매핑한다. salesChannelId + canonicalOptionValueId 조합은
 * 유일해야 한다.
 */
public class ChannelOptionMapping {

    private final ChannelOptionMappingId id;
    private final SalesChannelId salesChannelId;
    private final CanonicalOptionValueId canonicalOptionValueId;
    private ExternalOptionCode externalOptionCode;
    private final Instant createdAt;
    private Instant updatedAt;

    private ChannelOptionMapping(
            ChannelOptionMappingId id,
            SalesChannelId salesChannelId,
            CanonicalOptionValueId canonicalOptionValueId,
            ExternalOptionCode externalOptionCode,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.canonicalOptionValueId = canonicalOptionValueId;
        this.externalOptionCode = externalOptionCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 채널 옵션 매핑 생성. */
    public static ChannelOptionMapping forNew(
            SalesChannelId salesChannelId,
            CanonicalOptionValueId canonicalOptionValueId,
            ExternalOptionCode externalOptionCode,
            Instant now) {
        return new ChannelOptionMapping(
                ChannelOptionMappingId.forNew(),
                salesChannelId,
                canonicalOptionValueId,
                externalOptionCode,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ChannelOptionMapping reconstitute(
            ChannelOptionMappingId id,
            SalesChannelId salesChannelId,
            CanonicalOptionValueId canonicalOptionValueId,
            ExternalOptionCode externalOptionCode,
            Instant createdAt,
            Instant updatedAt) {
        return new ChannelOptionMapping(
                id,
                salesChannelId,
                canonicalOptionValueId,
                externalOptionCode,
                createdAt,
                updatedAt);
    }

    /** 외부 옵션 코드 변경. */
    public void updateExternalOptionCode(ExternalOptionCode externalOptionCode, Instant now) {
        this.externalOptionCode = externalOptionCode;
        this.updatedAt = now;
    }

    // ── Accessor 메서드 ──

    public ChannelOptionMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SalesChannelId salesChannelId() {
        return salesChannelId;
    }

    public Long salesChannelIdValue() {
        return salesChannelId.value();
    }

    public CanonicalOptionValueId canonicalOptionValueId() {
        return canonicalOptionValueId;
    }

    public Long canonicalOptionValueIdValue() {
        return canonicalOptionValueId.value();
    }

    public ExternalOptionCode externalOptionCode() {
        return externalOptionCode;
    }

    public String externalOptionCodeValue() {
        return externalOptionCode.value();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
