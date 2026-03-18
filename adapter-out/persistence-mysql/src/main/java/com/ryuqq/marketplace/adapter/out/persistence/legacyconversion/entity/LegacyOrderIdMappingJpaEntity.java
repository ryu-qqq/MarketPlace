package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * LegacyOrderIdMappingJpaEntity - 레거시 주문 ID 매핑 JPA 엔티티.
 *
 * <p>레거시 주문 ID와 내부 주문 ID 간의 매핑을 저장합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "legacy_order_id_mappings")
public class LegacyOrderIdMappingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_order_id", nullable = false, unique = true)
    private long legacyOrderId;

    @Column(name = "legacy_payment_id", nullable = false)
    private long legacyPaymentId;

    @Column(name = "internal_order_id", nullable = false)
    private String internalOrderId;

    @Column(name = "sales_channel_id", nullable = false)
    private long salesChannelId;

    @Column(name = "channel_name", nullable = false)
    private String channelName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LegacyOrderIdMappingJpaEntity() {}

    private LegacyOrderIdMappingJpaEntity(
            Long id,
            long legacyOrderId,
            long legacyPaymentId,
            String internalOrderId,
            long salesChannelId,
            String channelName,
            Instant createdAt) {
        this.id = id;
        this.legacyOrderId = legacyOrderId;
        this.legacyPaymentId = legacyPaymentId;
        this.internalOrderId = internalOrderId;
        this.salesChannelId = salesChannelId;
        this.channelName = channelName;
        this.createdAt = createdAt;
    }

    public static LegacyOrderIdMappingJpaEntity create(
            Long id,
            long legacyOrderId,
            long legacyPaymentId,
            String internalOrderId,
            long salesChannelId,
            String channelName,
            Instant createdAt) {
        return new LegacyOrderIdMappingJpaEntity(
                id,
                legacyOrderId,
                legacyPaymentId,
                internalOrderId,
                salesChannelId,
                channelName,
                createdAt);
    }

    public Long getId() {
        return id;
    }

    public long getLegacyOrderId() {
        return legacyOrderId;
    }

    public long getLegacyPaymentId() {
        return legacyPaymentId;
    }

    public String getInternalOrderId() {
        return internalOrderId;
    }

    public long getSalesChannelId() {
        return salesChannelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
