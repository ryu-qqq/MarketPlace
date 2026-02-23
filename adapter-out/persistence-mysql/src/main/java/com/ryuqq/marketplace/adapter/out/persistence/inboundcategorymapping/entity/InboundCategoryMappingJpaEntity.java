package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundCategoryMapping JPA 엔티티. */
@Entity
@Table(name = "inbound_category_mapping")
public class InboundCategoryMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inbound_source_id", nullable = false)
    private Long inboundSourceId;

    @Column(name = "external_category_code", nullable = false, length = 200)
    private String externalCategoryCode;

    @Column(name = "external_category_name", length = 500)
    private String externalCategoryName;

    @Column(name = "internal_category_id", nullable = false)
    private Long internalCategoryId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected InboundCategoryMappingJpaEntity() {
        super();
    }

    private InboundCategoryMappingJpaEntity(
            Long id,
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalCategoryCode = externalCategoryCode;
        this.externalCategoryName = externalCategoryName;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
    }

    public static InboundCategoryMappingJpaEntity create(
            Long id,
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundCategoryMappingJpaEntity(
                id,
                inboundSourceId,
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

    public Long getInboundSourceId() {
        return inboundSourceId;
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
