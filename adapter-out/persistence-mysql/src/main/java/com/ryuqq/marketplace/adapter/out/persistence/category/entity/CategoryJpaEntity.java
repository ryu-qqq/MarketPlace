package com.ryuqq.marketplace.adapter.out.persistence.category.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Category JPA 엔티티. */
@Entity
@Table(name = "category")
public class CategoryJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 100, unique = true)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 255)
    private String nameKo;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "depth", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    private int depth;

    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "leaf", nullable = false)
    private boolean leaf;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "department", nullable = false, length = 30)
    private String department;

    @Column(name = "category_group", nullable = false, length = 50)
    private String categoryGroup;

    protected CategoryJpaEntity() {
        super();
    }

    private CategoryJpaEntity(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String status,
            String department,
            String categoryGroup,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.parentId = parentId;
        this.depth = depth;
        this.path = path;
        this.sortOrder = sortOrder;
        this.leaf = leaf;
        this.status = status;
        this.department = department;
        this.categoryGroup = categoryGroup;
    }

    public static CategoryJpaEntity create(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String status,
            String department,
            String categoryGroup,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new CategoryJpaEntity(
                id,
                code,
                nameKo,
                nameEn,
                parentId,
                depth,
                path,
                sortOrder,
                leaf,
                status,
                department,
                categoryGroup,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameKo() {
        return nameKo;
    }

    public String getNameEn() {
        return nameEn;
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

    public String getDepartment() {
        return department;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }
}
