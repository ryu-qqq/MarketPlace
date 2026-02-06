package com.ryuqq.marketplace.adapter.out.persistence.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * SellerJpaEntity - 셀러 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 */
@Entity
@Table(name = "sellers")
public class SellerJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_name", nullable = false, length = 100, unique = true)
    private String sellerName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "auth_tenant_id", length = 100)
    private String authTenantId;

    @Column(name = "auth_organization_id", length = 100)
    private String authOrganizationId;

    protected SellerJpaEntity() {
        super();
    }

    private SellerJpaEntity(
            Long id,
            String sellerName,
            String displayName,
            String logoUrl,
            String description,
            boolean active,
            String authTenantId,
            String authOrganizationId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerName = sellerName;
        this.displayName = displayName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.active = active;
        this.authTenantId = authTenantId;
        this.authOrganizationId = authOrganizationId;
    }

    public static SellerJpaEntity create(
            Long id,
            String sellerName,
            String displayName,
            String logoUrl,
            String description,
            boolean active,
            String authTenantId,
            String authOrganizationId,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerJpaEntity(
                id,
                sellerName,
                displayName,
                logoUrl,
                description,
                active,
                authTenantId,
                authOrganizationId,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public String getAuthTenantId() {
        return authTenantId;
    }

    public String getAuthOrganizationId() {
        return authOrganizationId;
    }

    /**
     * 인증 정보 업데이트.
     *
     * @param authTenantId 인증 테넌트 ID
     * @param authOrganizationId 인증 조직 ID
     * @param now 업데이트 시각
     */
    public void updateAuthInfo(String authTenantId, String authOrganizationId, Instant now) {
        this.authTenantId = authTenantId;
        this.authOrganizationId = authOrganizationId;
        super.setUpdatedAt(now);
    }
}
