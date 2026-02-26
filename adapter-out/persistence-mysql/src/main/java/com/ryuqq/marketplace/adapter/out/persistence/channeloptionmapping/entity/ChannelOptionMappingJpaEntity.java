package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** ChannelOptionMapping JPA 엔티티. */
@Entity
@Table(name = "channel_option_mapping")
public class ChannelOptionMappingJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_channel_id", nullable = false)
    private Long salesChannelId;

    @Column(name = "canonical_option_value_id", nullable = false)
    private Long canonicalOptionValueId;

    @Column(name = "external_option_code", nullable = false, length = 100)
    private String externalOptionCode;

    protected ChannelOptionMappingJpaEntity() {
        super();
    }

    private ChannelOptionMappingJpaEntity(
            Long id,
            Long salesChannelId,
            Long canonicalOptionValueId,
            String externalOptionCode,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.canonicalOptionValueId = canonicalOptionValueId;
        this.externalOptionCode = externalOptionCode;
    }

    public static ChannelOptionMappingJpaEntity create(
            Long id,
            Long salesChannelId,
            Long canonicalOptionValueId,
            String externalOptionCode,
            Instant createdAt,
            Instant updatedAt) {
        return new ChannelOptionMappingJpaEntity(
                id,
                salesChannelId,
                canonicalOptionValueId,
                externalOptionCode,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSalesChannelId() {
        return salesChannelId;
    }

    public Long getCanonicalOptionValueId() {
        return canonicalOptionValueId;
    }

    public String getExternalOptionCode() {
        return externalOptionCode;
    }
}
