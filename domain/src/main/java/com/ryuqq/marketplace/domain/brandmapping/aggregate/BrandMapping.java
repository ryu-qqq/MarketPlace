package com.ryuqq.marketplace.domain.brandmapping.aggregate;

import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import java.time.Instant;

/** BrandMapping Aggregate Root. */
public class BrandMapping {

    private final BrandMappingId id;
    private final Long salesChannelBrandId;
    private final Long internalBrandId;
    private BrandMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private BrandMapping(
            BrandMappingId id,
            Long salesChannelBrandId,
            Long internalBrandId,
            BrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelBrandId = salesChannelBrandId;
        this.internalBrandId = internalBrandId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 BrandMapping 생성 팩토리. */
    public static BrandMapping forNew(Long salesChannelBrandId, Long internalBrandId, Instant now) {
        return new BrandMapping(
                BrandMappingId.forNew(),
                salesChannelBrandId,
                internalBrandId,
                BrandMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static BrandMapping reconstitute(
            BrandMappingId id,
            Long salesChannelBrandId,
            Long internalBrandId,
            BrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new BrandMapping(
                id, salesChannelBrandId, internalBrandId, status, createdAt, updatedAt);
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = BrandMappingStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = BrandMappingStatus.INACTIVE;
        this.updatedAt = now;
    }

    public BrandMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long salesChannelBrandId() {
        return salesChannelBrandId;
    }

    public Long internalBrandId() {
        return internalBrandId;
    }

    public BrandMappingStatus status() {
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
