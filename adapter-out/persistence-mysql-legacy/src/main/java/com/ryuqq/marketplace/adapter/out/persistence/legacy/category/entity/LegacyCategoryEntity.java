package com.ryuqq.marketplace.adapter.out.persistence.legacy.category.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyCategoryEntity - 레거시 카테고리 엔티티.
 *
 * <p>레거시 DB의 category 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Entity
@Table(name = "category")
public class LegacyCategoryEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "category_id")
    private Long id;

    @Column(name = "CATEGORY_NAME", length = 50)
    private String categoryName;

    @Column(name = "CATEGORY_DEPTH")
    private int categoryDepth;

    @Column(name = "PARENT_CATEGORY_ID")
    private long parentCategoryId;

    @Column(name = "DISPLAY_NAME", length = 50)
    private String displayName;

    @Column(name = "DISPLAY_YN", length = 1)
    private String displayYn;

    @Column(name = "PATH", length = 255)
    private String path;

    protected LegacyCategoryEntity() {}

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryDepth() {
        return categoryDepth;
    }

    public long getParentCategoryId() {
        return parentCategoryId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayYn() {
        return displayYn;
    }

    public String getPath() {
        return path;
    }
}
