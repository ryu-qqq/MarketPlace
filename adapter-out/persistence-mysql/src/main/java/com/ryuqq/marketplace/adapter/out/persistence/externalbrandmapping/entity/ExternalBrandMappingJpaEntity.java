package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** ExternalBrandMapping JPA 엔티티. */
@Entity
@Table(name = "external_brand_mapping")
public class ExternalBrandMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_source_id", nullable = false)
    private Long externalSourceId;

    @Column(name = "external_brand_code", nullable = false, length = 200)
    private String externalBrandCode;

    @Column(name = "external_brand_name", length = 500)
    private String externalBrandName;

    @Column(name = "internal_brand_id", nullable = false)
    private Long internalBrandId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected ExternalBrandMappingJpaEntity() {
        super();
    }

    private ExternalBrandMappingJpaEntity(
            Long id,
            Long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.externalSourceId = externalSourceId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.internalBrandId = internalBrandId;
        this.status = status;
    }

    public static ExternalBrandMappingJpaEntity create(
            Long id,
            Long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new ExternalBrandMappingJpaEntity(
                id,
                externalSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
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

    public String getExternalBrandCode() {
        return externalBrandCode;
    }

    public String getExternalBrandName() {
        return externalBrandName;
    }

    public Long getInternalBrandId() {
        return internalBrandId;
    }

    public String getStatus() {
        return status;
    }
}
