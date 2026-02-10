package com.ryuqq.marketplace.domain.saleschannelcategory.aggregate;

import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.time.Instant;

/** SalesChannelCategory Aggregate Root. */
public class SalesChannelCategory {

    private final SalesChannelCategoryId id;
    private final Long salesChannelId;
    private final String externalCategoryCode;
    private String externalCategoryName;
    private final Long parentId;
    private CategoryDepth depth;
    private CategoryPath path;
    private SortOrder sortOrder;
    private boolean leaf;
    private SalesChannelCategoryStatus status;
    private String displayPath;
    private final Instant createdAt;
    private Instant updatedAt;

    private SalesChannelCategory(
            SalesChannelCategoryId id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean leaf,
            SalesChannelCategoryStatus status,
            String displayPath,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.externalCategoryCode = externalCategoryCode;
        this.externalCategoryName = externalCategoryName;
        this.parentId = parentId;
        this.depth = depth;
        this.path = path;
        this.sortOrder = sortOrder;
        this.leaf = leaf;
        this.status = status;
        this.displayPath = displayPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SalesChannelCategory forNew(
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String displayPath,
            Instant now) {
        return new SalesChannelCategory(
                SalesChannelCategoryId.forNew(),
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                parentId,
                CategoryDepth.of(depth),
                CategoryPath.of(path),
                SortOrder.of(sortOrder),
                leaf,
                SalesChannelCategoryStatus.ACTIVE,
                displayPath,
                now,
                now);
    }

    public static SalesChannelCategory reconstitute(
            SalesChannelCategoryId id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            SalesChannelCategoryStatus status,
            String displayPath,
            Instant createdAt,
            Instant updatedAt) {
        return new SalesChannelCategory(
                id,
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                parentId,
                CategoryDepth.of(depth),
                CategoryPath.of(path),
                SortOrder.of(sortOrder),
                leaf,
                status,
                displayPath,
                createdAt,
                updatedAt);
    }

    public void update(SalesChannelCategoryUpdateData updateData, Instant now) {
        this.externalCategoryName = updateData.externalCategoryName();
        this.sortOrder = SortOrder.of(updateData.sortOrder());
        this.leaf = updateData.leaf();
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public SalesChannelCategoryId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long salesChannelId() {
        return salesChannelId;
    }

    public String externalCategoryCode() {
        return externalCategoryCode;
    }

    public String externalCategoryName() {
        return externalCategoryName;
    }

    public Long parentId() {
        return parentId;
    }

    public int depth() {
        return depth.value();
    }

    public String path() {
        return path.value();
    }

    public int sortOrder() {
        return sortOrder.value();
    }

    public boolean isLeaf() {
        return leaf;
    }

    public SalesChannelCategoryStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public String displayPath() {
        return displayPath;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
