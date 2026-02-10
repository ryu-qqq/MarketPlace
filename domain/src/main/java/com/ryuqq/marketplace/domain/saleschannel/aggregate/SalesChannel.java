package com.ryuqq.marketplace.domain.saleschannel.aggregate;

import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.ChannelName;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.time.Instant;

/** 판매채널 Aggregate Root. */
public class SalesChannel {

    private final SalesChannelId id;
    private ChannelName channelName;
    private SalesChannelStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private SalesChannel(
            SalesChannelId id,
            ChannelName channelName,
            SalesChannelStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.channelName = channelName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SalesChannel forNew(String channelName, Instant now) {
        return new SalesChannel(
                SalesChannelId.forNew(),
                ChannelName.of(channelName),
                SalesChannelStatus.ACTIVE,
                now,
                now);
    }

    public static SalesChannel reconstitute(
            SalesChannelId id,
            String channelName,
            SalesChannelStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new SalesChannel(id, ChannelName.of(channelName), status, createdAt, updatedAt);
    }

    public void update(SalesChannelUpdateData updateData, Instant now) {
        this.channelName = ChannelName.of(updateData.channelName());
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.status = SalesChannelStatus.ACTIVE;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.status = SalesChannelStatus.INACTIVE;
        this.updatedAt = now;
    }

    public SalesChannelId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public String channelName() {
        return channelName.value();
    }

    public SalesChannelStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
