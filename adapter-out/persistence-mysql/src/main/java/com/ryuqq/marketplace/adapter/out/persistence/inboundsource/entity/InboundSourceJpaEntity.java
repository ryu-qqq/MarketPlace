package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundSource JPA 엔티티. */
@Entity
@Table(name = "inbound_source")
public class InboundSourceJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "description", length = 1000)
    private String description;

    protected InboundSourceJpaEntity() {
        super();
    }

    private InboundSourceJpaEntity(
            Long id,
            String code,
            String name,
            String type,
            String status,
            String description,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public static InboundSourceJpaEntity create(
            Long id,
            String code,
            String name,
            String type,
            String status,
            String description,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundSourceJpaEntity(
                id, code, name, type, status, description, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
