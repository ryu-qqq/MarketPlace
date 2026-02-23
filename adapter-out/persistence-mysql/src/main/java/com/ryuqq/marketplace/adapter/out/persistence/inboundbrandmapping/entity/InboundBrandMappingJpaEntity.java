package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundBrandMapping JPA 엔티티. */
@Entity
@Table(name = "inbound_brand_mapping")
public class InboundBrandMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inbound_source_id", nullable = false)
    private Long inboundSourceId;

    @Column(name = "external_brand_code", nullable = false, length = 200)
    private String externalBrandCode;

    @Column(name = "external_brand_name", length = 500)
    private String externalBrandName;

    @Column(name = "internal_brand_id", nullable = false)
    private Long internalBrandId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected InboundBrandMappingJpaEntity() {
        super();
    }

    private InboundBrandMappingJpaEntity(
            Long id,
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.internalBrandId = internalBrandId;
        this.status = status;
    }

    public static InboundBrandMappingJpaEntity create(
            Long id,
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundBrandMappingJpaEntity(
                id,
                inboundSourceId,
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

    public Long getInboundSourceId() {
        return inboundSourceId;
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
