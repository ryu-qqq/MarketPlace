package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** SalesChannelCategory JPA 엔티티. */
@Entity
@Table(name = "sales_channel_category")
public class SalesChannelCategoryJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "external_category_code", nullable = false, length = 200)
    private String externalCategoryCode;

    @Column(name = "external_category_name", nullable = false, length = 500)
    private String externalCategoryName;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "depth", nullable = false)
    private int depth;

    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "leaf", nullable = false)
    private boolean leaf;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "display_path", length = 2000)
    private String displayPath;

    protected SalesChannelCategoryJpaEntity() {
        super();
    }

    private SalesChannelCategoryJpaEntity(
            Long id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String status,
            String displayPath,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
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
    }

    public static SalesChannelCategoryJpaEntity create(
            Long id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String status,
            String displayPath,
            Instant createdAt,
            Instant updatedAt) {
        return new SalesChannelCategoryJpaEntity(
                id,
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                parentId,
                depth,
                path,
                sortOrder,
                leaf,
                status,
                displayPath,
                createdAt,
                updatedAt);
    }

    public void update(
            String externalCategoryName,
            int sortOrder,
            boolean leaf,
            String status,
            Instant updatedAt) {
        this.externalCategoryName = externalCategoryName;
        this.sortOrder = sortOrder;
        this.leaf = leaf;
        this.status = status;
        setUpdatedAt(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public String getExternalCategoryCode() {
        return externalCategoryCode;
    }

    public String getExternalCategoryName() {
        return externalCategoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public int getDepth() {
        return depth;
    }

    public String getPath() {
        return path;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayPath() {
        return displayPath;
    }
}
