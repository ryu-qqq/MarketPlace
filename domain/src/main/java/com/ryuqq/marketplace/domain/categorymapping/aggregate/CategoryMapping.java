package com.ryuqq.marketplace.domain.categorymapping.aggregate;

import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import java.time.Instant;

/** CategoryMapping Aggregate Root. */
public class CategoryMapping {

    private final CategoryMappingId id;
    private final Long salesChannelCategoryId;
    private final Long internalCategoryId;
    private CategoryMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private CategoryMapping(
            CategoryMappingId id,
            Long salesChannelCategoryId,
            Long internalCategoryId,
            CategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelCategoryId = salesChannelCategoryId;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 CategoryMapping 생성 팩토리. */
    public static CategoryMapping forNew(
            Long salesChannelCategoryId, Long internalCategoryId, Instant now) {
        return new CategoryMapping(
                CategoryMappingId.forNew(),
                salesChannelCategoryId,
                internalCategoryId,
                CategoryMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static CategoryMapping reconstitute(
            CategoryMappingId id,
            Long salesChannelCategoryId,
            Long internalCategoryId,
            CategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new CategoryMapping(
                id, salesChannelCategoryId, internalCategoryId, status, createdAt, updatedAt);
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = CategoryMappingStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = CategoryMappingStatus.INACTIVE;
        this.updatedAt = now;
    }

    public CategoryMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long salesChannelCategoryId() {
        return salesChannelCategoryId;
    }

    public Long internalCategoryId() {
        return internalCategoryId;
    }

    public CategoryMappingStatus status() {
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
