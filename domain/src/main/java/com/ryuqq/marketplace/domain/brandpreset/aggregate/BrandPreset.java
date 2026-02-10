package com.ryuqq.marketplace.domain.brandpreset.aggregate;

import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.vo.BrandPresetStatus;
import java.time.Instant;

/** BrandPreset Aggregate Root. */
public class BrandPreset {

    private final BrandPresetId id;
    private final Long shopId;
    private Long salesChannelBrandId;
    private String presetName;
    private BrandPresetStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private BrandPreset(
            BrandPresetId id,
            Long shopId,
            Long salesChannelBrandId,
            String presetName,
            BrandPresetStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.shopId = shopId;
        this.salesChannelBrandId = salesChannelBrandId;
        this.presetName = presetName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 BrandPreset 생성 팩토리. */
    public static BrandPreset forNew(
            Long shopId, Long salesChannelBrandId, String presetName, Instant now) {
        return new BrandPreset(
                BrandPresetId.forNew(),
                shopId,
                salesChannelBrandId,
                presetName,
                BrandPresetStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static BrandPreset reconstitute(
            BrandPresetId id,
            Long shopId,
            Long salesChannelBrandId,
            String presetName,
            BrandPresetStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new BrandPreset(
                id, shopId, salesChannelBrandId, presetName, status, createdAt, updatedAt);
    }

    /** 프리셋 정보 수정. */
    public void update(String presetName, Long salesChannelBrandId, Instant now) {
        this.presetName = presetName;
        this.salesChannelBrandId = salesChannelBrandId;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = BrandPresetStatus.INACTIVE;
        this.updatedAt = now;
    }

    public BrandPresetId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long shopId() {
        return shopId;
    }

    public Long salesChannelBrandId() {
        return salesChannelBrandId;
    }

    public String presetName() {
        return presetName;
    }

    public BrandPresetStatus status() {
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
