package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** ExternalCategoryMapping JPA 엔티티. */
@Entity
@Table(name = "external_category_mapping")
public class ExternalCategoryMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_source_id", nullable = false)
    private Long externalSourceId;

    @Column(name = "external_category_code", nullable = false, length = 200)
    private String externalCategoryCode;

    @Column(name = "external_category_name", length = 500)
    private String externalCategoryName;

    @Column(name = "internal_category_id", nullable = false)
    private Long internalCategoryId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected ExternalCategoryMappingJpaEntity() {
        super();
    }

    private ExternalCategoryMappingJpaEntity(
            Long id,
            Long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.externalSourceId = externalSourceId;
        this.externalCategoryCode = externalCategoryCode;
        this.externalCategoryName = externalCategoryName;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
    }

    public static ExternalCategoryMappingJpaEntity create(
            Long id,
            Long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new ExternalCategoryMappingJpaEntity(
                id,
                externalSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                status,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getExternalSourceId() {
        return externalSourceId;
    }

    public String getExternalCategoryCode() {
        return externalCategoryCode;
    }

    public String getExternalCategoryName() {
        return externalCategoryName;
    }

    public Long getInternalCategoryId() {
        return internalCategoryId;
    }

    public String getStatus() {
        return status;
    }
}
