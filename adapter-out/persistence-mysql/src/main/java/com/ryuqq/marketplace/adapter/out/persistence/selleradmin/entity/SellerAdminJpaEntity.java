package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * SellerAdminJpaEntity - 셀러 관리자 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 *
 * <p>ID는 UUIDv7 String으로 외부에서 생성됩니다.
 */
@Entity
@Table(name = "seller_admins")
public class SellerAdminJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "auth_user_id", length = 100)
    private String authUserId;

    @Column(name = "login_id", nullable = false, length = 100, unique = true)
    private String loginId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SellerAdminStatus status;

    protected SellerAdminJpaEntity() {
        super();
    }

    private SellerAdminJpaEntity(
            String id,
            Long sellerId,
            String authUserId,
            String loginId,
            String name,
            String phoneNumber,
            SellerAdminStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.authUserId = authUserId;
        this.loginId = loginId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public static SellerAdminJpaEntity create(
            String id,
            Long sellerId,
            String authUserId,
            String loginId,
            String name,
            String phoneNumber,
            SellerAdminStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerAdminJpaEntity(
                id,
                sellerId,
                authUserId,
                loginId,
                name,
                phoneNumber,
                status,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public String getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public SellerAdminStatus getStatus() {
        return status;
    }
}
