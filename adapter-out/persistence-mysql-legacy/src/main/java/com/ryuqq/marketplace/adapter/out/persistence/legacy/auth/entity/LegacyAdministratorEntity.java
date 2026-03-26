package com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** luxurydb administrators 테이블 매핑. */
@Entity
@Table(name = "administrators")
public class LegacyAdministratorEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "SELLER_ID", nullable = false)
    private Long sellerId;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 60)
    private String passwordHash;

    @Column(name = "FULL_NAME", nullable = false, length = 45)
    private String fullName;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "APPROVAL_STATUS", nullable = false, length = 45)
    private String approvalStatus;

    @Column(name = "DELETE_YN", nullable = false, length = 1)
    private String deleteYn;

    protected LegacyAdministratorEntity() {}

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
