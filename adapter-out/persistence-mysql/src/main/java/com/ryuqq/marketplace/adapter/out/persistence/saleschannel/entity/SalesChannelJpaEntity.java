package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** SalesChannel JPA 엔티티. */
@Entity
@Table(name = "sales_channel")
public class SalesChannelJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_name", nullable = false, length = 100)
    private String channelName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected SalesChannelJpaEntity() {
        super();
    }

    private SalesChannelJpaEntity(
            Long id, String channelName, String status, Instant createdAt, Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.channelName = channelName;
        this.status = status;
    }

    public static SalesChannelJpaEntity create(
            Long id, String channelName, String status, Instant createdAt, Instant updatedAt) {
        return new SalesChannelJpaEntity(id, channelName, status, createdAt, updatedAt);
    }

    public void update(String channelName, String status, Instant updatedAt) {
        this.channelName = channelName;
        this.status = status;
        setUpdatedAt(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getStatus() {
        return status;
    }
}
