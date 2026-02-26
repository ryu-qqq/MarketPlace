package com.ryuqq.marketplace.adapter.out.persistence.notice.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** NoticeField JPA 엔티티. */
@Entity
@Table(name = "notice_field")
public class NoticeFieldJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notice_category_id", nullable = false)
    private Long noticeCategoryId;

    @Column(name = "field_code", nullable = false, length = 50)
    private String fieldCode;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected NoticeFieldJpaEntity() {
        super();
    }

    private NoticeFieldJpaEntity(
            Long id,
            Long noticeCategoryId,
            String fieldCode,
            String fieldName,
            boolean required,
            int sortOrder,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.noticeCategoryId = noticeCategoryId;
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.required = required;
        this.sortOrder = sortOrder;
    }

    public static NoticeFieldJpaEntity create(
            Long id,
            Long noticeCategoryId,
            String fieldCode,
            String fieldName,
            boolean required,
            int sortOrder,
            Instant createdAt,
            Instant updatedAt) {
        return new NoticeFieldJpaEntity(
                id,
                noticeCategoryId,
                fieldCode,
                fieldName,
                required,
                sortOrder,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getNoticeCategoryId() {
        return noticeCategoryId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isRequired() {
        return required;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
