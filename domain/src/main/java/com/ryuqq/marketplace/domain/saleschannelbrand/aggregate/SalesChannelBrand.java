package com.ryuqq.marketplace.domain.saleschannelbrand.aggregate;

import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.time.Instant;

/** SalesChannelBrand Aggregate Root. */
public class SalesChannelBrand {

    private final SalesChannelBrandId id;
    private final Long salesChannelId;
    private final String externalBrandCode;
    private String externalBrandName;
    private SalesChannelBrandStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private SalesChannelBrand(
            SalesChannelBrandId id,
            Long salesChannelId,
            String externalBrandCode,
            String externalBrandName,
            SalesChannelBrandStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SalesChannelBrand forNew(
            Long salesChannelId, String externalBrandCode, String externalBrandName, Instant now) {
        return new SalesChannelBrand(
                SalesChannelBrandId.forNew(),
                salesChannelId,
                externalBrandCode,
                externalBrandName,
                SalesChannelBrandStatus.ACTIVE,
                now,
                now);
    }

    public static SalesChannelBrand reconstitute(
            SalesChannelBrandId id,
            Long salesChannelId,
            String externalBrandCode,
            String externalBrandName,
            SalesChannelBrandStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new SalesChannelBrand(
                id,
                salesChannelId,
                externalBrandCode,
                externalBrandName,
                status,
                createdAt,
                updatedAt);
    }

    public SalesChannelBrandId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long salesChannelId() {
        return salesChannelId;
    }

    public String externalBrandCode() {
        return externalBrandCode;
    }

    public String externalBrandName() {
        return externalBrandName;
    }

    public SalesChannelBrandStatus status() {
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
