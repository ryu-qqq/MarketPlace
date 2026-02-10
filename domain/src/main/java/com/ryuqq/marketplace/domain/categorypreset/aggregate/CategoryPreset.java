package com.ryuqq.marketplace.domain.categorypreset.aggregate;

import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.vo.CategoryPresetStatus;
import java.time.Instant;

/** CategoryPreset Aggregate Root. */
public class CategoryPreset {

    private final CategoryPresetId id;
    private final Long shopId;
    private Long salesChannelCategoryId;
    private String presetName;
    private CategoryPresetStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private CategoryPreset(
            CategoryPresetId id,
            Long shopId,
            Long salesChannelCategoryId,
            String presetName,
            CategoryPresetStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.shopId = shopId;
        this.salesChannelCategoryId = salesChannelCategoryId;
        this.presetName = presetName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 CategoryPreset 생성 팩토리. */
    public static CategoryPreset forNew(
            Long shopId, Long salesChannelCategoryId, String presetName, Instant now) {
        return new CategoryPreset(
                CategoryPresetId.forNew(),
                shopId,
                salesChannelCategoryId,
                presetName,
                CategoryPresetStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static CategoryPreset reconstitute(
            CategoryPresetId id,
            Long shopId,
            Long salesChannelCategoryId,
            String presetName,
            CategoryPresetStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new CategoryPreset(
                id, shopId, salesChannelCategoryId, presetName, status, createdAt, updatedAt);
    }

    /** 프리셋 정보 수정. */
    public void update(String presetName, Long salesChannelCategoryId, Instant now) {
        this.presetName = presetName;
        this.salesChannelCategoryId = salesChannelCategoryId;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = CategoryPresetStatus.INACTIVE;
        this.updatedAt = now;
    }

    public CategoryPresetId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long shopId() {
        return shopId;
    }

    public Long salesChannelCategoryId() {
        return salesChannelCategoryId;
    }

    public String presetName() {
        return presetName;
    }

    public CategoryPresetStatus status() {
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
